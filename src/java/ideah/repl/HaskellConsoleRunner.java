package ideah.repl;

import com.intellij.execution.*;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.console.ConsoleHistoryController;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public final class HaskellConsoleRunner {

    static final String REPL_TITLE = "GHCi";

    static final String EXECUTE_ACTION_IMMEDIATELY_ID = "Haskell.Console.Execute.Immediately";
    static final String EXECUTE_ACTION_ID = "Haskell.Console.Execute";

    private final Module module;
    private final Project project;
    private final String consoleTitle;
    private final String workingDir;
    private final ConsoleHistoryModel historyModel;

    private HaskellConsoleView consoleView;
    private ProcessHandler processHandler;

    private HaskellConsoleExecuteActionHandler executeHandler;
    private AnAction runAction;

    private HaskellConsoleRunner(@NotNull Module module,
                                 @NotNull String consoleTitle,
                                 @Nullable String workingDir) {
        this.module = module;
        this.project = module.getProject();
        this.consoleTitle = consoleTitle;
        this.workingDir = workingDir;
        this.historyModel = new ConsoleHistoryModel();
    }

    public static void run(@NotNull Module module,
                           String workingDir,
                           String... statements2execute) {
        HaskellConsoleRunner runner = new HaskellConsoleRunner(module, REPL_TITLE, workingDir);
        try {
            runner.initAndRun(statements2execute);
        } catch (ExecutionException e) {
            ExecutionHelper.showErrors(module.getProject(), Arrays.<Exception>asList(e), REPL_TITLE, null);
        }
    }

    private void initAndRun(String... statements2execute) throws ExecutionException {
        // Create Server process
        GeneralCommandLine cmdline = createCommandLine(module, workingDir);
        Process process = cmdline.createProcess();
        // !!! do not change order!!!
        consoleView = createConsoleView();
        String commandLine = cmdline.getCommandLineString();
        processHandler = new HaskellConsoleProcessHandler(process, commandLine, getLanguageConsole());
        executeHandler = new HaskellConsoleExecuteActionHandler(processHandler, project, false);
        getLanguageConsole().setExecuteHandler(executeHandler);

        // Init a console view
        ProcessTerminatedListener.attach(processHandler);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(ProcessEvent event) {
                runAction.getTemplatePresentation().setEnabled(false);
                consoleView.getConsole().setPrompt("");
                consoleView.getConsole().getConsoleEditor().setRendererMode(true);
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        consoleView.getConsole().getConsoleEditor().getComponent().updateUI();
                    }
                });
            }
        });

        // Attach a console view to the process
        consoleView.attachToProcess(processHandler);

        // Runner creating
        Executor defaultExecutor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(actionToolbar.getComponent(), BorderLayout.WEST);
        panel.add(consoleView.getComponent(), BorderLayout.CENTER);

        RunContentDescriptor myDescriptor =
            new RunContentDescriptor(consoleView, processHandler, panel, consoleTitle);

        // tool bar actions
        AnAction[] actions = fillToolBarActions(toolbarActions, defaultExecutor, myDescriptor);
        registerActionShortcuts(actions, getLanguageConsole().getConsoleEditor().getComponent());
        registerActionShortcuts(actions, panel);
        panel.updateUI();

        // enter action
        //createAndRegisterEnterAction(panel);

        // Show in run tool window
        ExecutionManager.getInstance(project).getContentManager().showRunContent(defaultExecutor, myDescriptor);

        // Request focus
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(defaultExecutor.getId());
        window.activate(new Runnable() {
            public void run() {
                IdeFocusManager.getInstance(project).requestFocus(getLanguageConsole().getCurrentEditor().getContentComponent(), true);
            }
        });

        // Run
        processHandler.startNotify();

        HaskellConsole console = consoleView.getConsole();
        for (String statement : statements2execute) {
            String st = statement + "\n";
            HaskellConsoleHighlightingUtil.processOutput(console, st, ProcessOutputTypes.SYSTEM);
            executeHandler.processLine(st);
        }

    }

    private static void registerActionShortcuts(AnAction[] actions, JComponent component) {
        for (AnAction action : actions) {
            if (action.getShortcutSet() != null) {
                action.registerCustomShortcutSet(action.getShortcutSet(), component);
            }
        }
    }

    private AnAction[] fillToolBarActions(DefaultActionGroup toolbarActions,
                                          Executor defaultExecutor,
                                          RunContentDescriptor myDescriptor) {

        ArrayList<AnAction> actionList = new ArrayList<AnAction>();

        //stop
        AnAction stopAction = createStopAction();
        actionList.add(stopAction);

        //close
        AnAction closeAction = createCloseAction(defaultExecutor, myDescriptor);
        actionList.add(closeAction);

        // run and history actions
        ArrayList<AnAction> executionActions = createConsoleExecActions(getLanguageConsole(),
            processHandler, executeHandler, historyModel);
        runAction = executionActions.get(0);
        actionList.addAll(executionActions);

        // help action
        actionList.add(CommonActionsManager.getInstance().createHelpAction("interactive_console"));

        AnAction[] actions = actionList.toArray(new AnAction[actionList.size()]);
        toolbarActions.addAll(actions);
        return actions;
    }

    private static ArrayList<AnAction> createConsoleExecActions(HaskellConsole languageConsole,
                                                                ProcessHandler processHandler,
                                                                HaskellConsoleExecuteActionHandler executeHandler,
                                                                ConsoleHistoryModel historyModel) {
        AnAction runImmediatelyAction = new HaskellExecuteImmediatelyAction(languageConsole, processHandler, executeHandler);

        ConsoleHistoryController historyController = new ConsoleHistoryController("haskell", null, languageConsole, historyModel);
        historyController.install();

        AnAction upAction = historyController.getHistoryPrev();
        AnAction downAction = historyController.getHistoryNext();

        ArrayList<AnAction> list = new ArrayList<AnAction>();
        list.add(runImmediatelyAction);
        list.add(downAction);
        list.add(upAction);
//        list.add(enterAction);
        return list;
    }

    private AnAction createCloseAction(Executor defaultExecutor, RunContentDescriptor myDescriptor) {
        return new CloseAction(defaultExecutor, myDescriptor, project);
    }

    private AnAction createStopAction() {
        return ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM);
    }

    private HaskellConsoleView createConsoleView() {
        return new HaskellConsoleView(project, consoleTitle, historyModel);
    }

    private GeneralCommandLine createCommandLine(Module module, String workingDir) throws CantRunException {
        GeneralCommandLine line = new GeneralCommandLine();
        line.setExePath("C:\\Haskell\\bin\\ghci.exe"); // todo
        // todo: create command line for ghci
        line.setWorkDirectory(workingDir);
        return line;
    }

    private HaskellConsole getLanguageConsole() {
        return consoleView.getConsole();
    }
}
