package ideah;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.consulo.haskell.HaskellIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.Charset;

public final class HaskellFileType extends LanguageFileType {

    public static final HaskellFileType INSTANCE = new HaskellFileType();
    public static final Language HASKELL_LANGUAGE = INSTANCE.getLanguage();
	@Deprecated
    public static final Icon HASKELL_ICON = HaskellIcons.Haskell16x16;

    private static final String HASKELL_CHARSET_NAME = "UTF8";
    public static final Charset HASKELL_CHARSET = Charset.forName(HASKELL_CHARSET_NAME);

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
        return HASKELL_ICON;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, byte[] content) {
        return HASKELL_CHARSET_NAME;
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable Project project, @Nullable VirtualFile file, @NotNull String content) {
        return HASKELL_CHARSET;
    }
}
