package ideah;

import java.nio.charset.Charset;

import javax.swing.Icon;

import consulo.haskell.HaskellIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;

public final class HaskellFileType extends LanguageFileType {

    public static final HaskellFileType INSTANCE = new HaskellFileType();
    public static final Language HASKELL_LANGUAGE = INSTANCE.getLanguage();
	@Deprecated
    public static final Icon HASKELL_ICON = HaskellIcons.Haskell16x16;

    private static final String HASKELL_CHARSET_NAME = CharsetToolkit.UTF8;
    public static final Charset HASKELL_CHARSET = CharsetToolkit.UTF8_CHARSET;

    public HaskellFileType() {
        super(HaskellLanguage.INSTANCE);
    }

    @NotNull
    public String getName() {
        return "Haskell";
    }

    @NotNull
    public String getDescription() {
        return "Haskell files";
    }

    @NotNull
    public String getDefaultExtension() {
        return "hs";
    }

    public Icon getIcon() {
        return HaskellIcons.Haskell16x16;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return HASKELL_CHARSET_NAME;
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable Project project, @Nullable VirtualFile file, @NotNull CharSequence content) {
        return HASKELL_CHARSET;
    }
}