package ideah.compiler;

import javax.annotation.Nonnull;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.ui.image.Image;

public final class HiFileType implements FileType {

    public static final HiFileType INSTANCE = new HiFileType();

    @Nonnull
    public String getId() {
        return "Haskell interface";
    }

    @Nonnull
    public String getDescription() {
        return "Haskell interface file";
    }

    @Nonnull
    public String getDefaultExtension() {
        return "hi";
    }

    public Image getIcon() {
        return null;
    }

    public boolean isBinary() {
        return true;
    }

    public boolean isReadOnly() {
        return true;
    }

    public String getCharset(@Nonnull VirtualFile file, byte[] content) {
        return null;
    }
}
