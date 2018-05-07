package ideah.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import ideah.psi.api.HPAbstractIdent;
import javax.annotation.Nonnull;

public final class HaskellRenameProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@Nonnull PsiElement element) {
        return element instanceof HPAbstractIdent;
    }

    @Override
    public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        return new HaskellRenameDialog(project, element, nameSuggestionContext, editor);
    }
}
