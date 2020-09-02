package ideah.repl;

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.EmptyAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.IconLoader;
import consulo.haskell.HaskellIcons;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;

abstract class HaskellExecuteActionBase extends DumbAwareAction {
    protected final HaskellConsoleView console;
    protected final ProcessHandler processHandler;
    protected final HaskellConsoleExecuteActionHandler executeHandler;

    protected HaskellExecuteActionBase(HaskellConsoleView languageConsole,
                                       ProcessHandler processHandler,
                                       HaskellConsoleExecuteActionHandler executeHandler,
                                       String actionId) {
        super(LocalizeValue.empty(), LocalizeValue.empty(), HaskellIcons.Haskell16x16);
        this.console = languageConsole;
        this.processHandler = processHandler;
        this.executeHandler = executeHandler;
        EmptyAction.setupAction(this, actionId, null);
    }

    public void update(AnActionEvent e) {
        EditorEx editor = console.getConsoleEditor();
        Lookup lookup = LookupManager.getActiveLookup(editor);
        e.getPresentation().setEnabled(!processHandler.isProcessTerminated() &&
            (lookup == null || !lookup.isCompletion()));
    }
}
