package ideah.repl;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.openapi.project.Project;
import ideah.HaskellFileType;

public final class HaskellConsole extends LanguageConsoleImpl {

    private final ConsoleHistoryModel historyModel;
    private HaskellConsoleExecuteActionHandler executeHandler;

    HaskellConsole(Project project,
                   String title,
                   ConsoleHistoryModel historyModel) {
        super(project, title, HaskellFileType.HASKELL_LANGUAGE);
        this.historyModel = historyModel;
    }

    ConsoleHistoryModel getHistoryModel() {
        return historyModel;
    }

    public HaskellConsoleExecuteActionHandler getExecuteHandler() {
        return executeHandler;
    }

    void setExecuteHandler(HaskellConsoleExecuteActionHandler executeHandler) {
        this.executeHandler = executeHandler;
    }
}
