package ideah.parser;

import javax.annotation.Nonnull;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElementVisitor;
import ideah.HaskellFileType;

public final class HaskellFileImpl extends PsiFileBase implements HaskellFile {

    public HaskellFileImpl(@Nonnull FileViewProvider provider) {
        super(provider, HaskellFileType.HASKELL_LANGUAGE);
    }

    @Nonnull
    public FileType getFileType() {
        return HaskellFileType.INSTANCE;
    }

    public void accept(@Nonnull PsiElementVisitor visitor) {
        visitor.visitFile(this); // todo
    }
}
