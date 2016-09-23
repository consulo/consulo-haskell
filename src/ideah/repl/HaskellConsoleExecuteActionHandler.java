package ideah.repl;

import java.io.IOException;
import java.io.OutputStream;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;

public final class HaskellConsoleExecuteActionHandler {

    private final ProcessHandler processHandler;
    private final Project project;
    //private final IndentHelper myIndentHelper;
    private final boolean preserveMarkup;

    HaskellConsoleExecuteActionHandler(ProcessHandler processHandler,
                                       Project project,
                                       boolean preserveMarkup) {
        this.processHandler = processHandler;
        this.project = project;
        this.preserveMarkup = preserveMarkup;
        //myIndentHelper = IndentHelper.getInstance();
    }

    public void processLine(String line) {
        OutputStream os = processHandler.getProcessInput();
        if (os != null) {
            try {
                byte[] bytes = (line + "\n").getBytes();
                os.write(bytes);
                os.flush();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public void runExecuteAction(final HaskellConsoleView console,
                                 boolean executeImmediately) {
        if (executeImmediately) {
            execute(console);
            return;
        }

        // Process input and add to history
        Editor editor = console.getCurrentEditor();
        Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();
        final int offset = caretModel.getOffset();
        String text = document.getText();

        if (!"".equals(text.substring(offset).trim())) {
            String before = text.substring(0, offset);
            String after = text.substring(offset);
            final int indent = 0;
            String spaces = StringUtil.repeatSymbol(' ', indent);
            final String newText = before + "\n" + spaces + after;

            new WriteCommandAction(project) {
                @Override
                protected void run(Result result) throws Throwable {
                    console.setInputText(newText);
                    caretModel.moveToOffset(offset + indent + 1);
                }
            }.execute();

            return;
        }

//        String candidate = text.trim();
//        // S-expression contains no syntax errors
//        if (ClojurePsiUtil.isValidClojureExpression(candidate, myProject) || "".equals(candidate)) {
//            execute(console, consoleHistoryModel);
//        } else {
//            console.setInputText(text + "\n");
//        }
        execute(console);
    }

    private void execute(LanguageConsoleImpl languageConsole) {
        // Process input and add to history
        Document document = languageConsole.getCurrentEditor().getDocument();
        String text = document.getText();
        TextRange range = new TextRange(0, document.getTextLength());

        languageConsole.getCurrentEditor().getSelectionModel().setSelection(range.getStartOffset(), range.getEndOffset());
        languageConsole.addToHistory(range, languageConsole.getCurrentEditor(), preserveMarkup);
        languageConsole.setInputText("");

        // Send to interpreter / server
        processLine(text);
    }
}
