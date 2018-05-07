package ideah.lexer;

import com.intellij.psi.tree.IElementType;
import ideah.HaskellFileType;
import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;

public final class HaskellTokenType extends IElementType {

    private final String debugName;

    public HaskellTokenType(@Nonnull @NonNls String debugName) {
        super(debugName, HaskellFileType.HASKELL_LANGUAGE);
        this.debugName = debugName;
    }

    @Override
    public String toString() {
        return debugName;
    }
}
