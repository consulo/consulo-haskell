package ideah.psi.api.util;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import ideah.HaskellFileType;
import ideah.parser.HaskellFile;

import javax.annotation.Nullable;

public final class HaskellPsiElementFactoryImpl extends HaskellPsiElementFactory {

    private final Project myProject;

    public HaskellPsiElementFactoryImpl(Project project) {
        myProject = project;
    }

    private static final String DUMMY = "DUMMY.";

    @Nullable
    public ASTNode createIdentNodeFromText(@Nonnull String newName) {
        HaskellFile dummyFile = createHaskellFileFromText(newName);
        PsiElement firstChild = dummyFile.getFirstChild();
        if (firstChild != null)
            return firstChild.getNode();
        return null;
    }

    private HaskellFile createHaskellFileFromText(String text) {
        return (HaskellFile) PsiFileFactory.getInstance(myProject).createFileFromText(DUMMY + HaskellFileType.INSTANCE.getDefaultExtension(), HaskellFileType.HASKELL_LANGUAGE, text);
    }
}
