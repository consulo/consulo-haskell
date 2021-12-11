package ideah;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.haskell.icon.HaskellIconGroup;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;

public final class HaskellFileType extends LanguageFileType {

    public static final HaskellFileType INSTANCE = new HaskellFileType();
    public static final Language HASKELL_LANGUAGE = INSTANCE.getLanguage();
	@Deprecated
    public static final Image HASKELL_ICON = HaskellIconGroup.haskell();

    private static final String HASKELL_CHARSET_NAME = CharsetToolkit.UTF8;
    public static final Charset HASKELL_CHARSET = CharsetToolkit.UTF8_CHARSET;

    public HaskellFileType() {
        super(HaskellLanguage.INSTANCE);
    }

    @Nonnull
    public String getId() {
        return "Haskell";
    }

    @Nonnull
    public LocalizeValue getDescription() {
        return LocalizeValue.localizeTODO("Haskell files");
    }

    @Nonnull
    public String getDefaultExtension() {
        return "hs";
    }

    public Image getIcon() {
        return HaskellIconGroup.haskell();
    }

    @Override
    public String getCharset(@Nonnull VirtualFile file, byte[] content) {
        return HASKELL_CHARSET_NAME;
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable Project project, @Nullable VirtualFile file, @Nonnull CharSequence content) {
        return HASKELL_CHARSET;
    }
}
