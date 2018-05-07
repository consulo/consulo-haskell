package ideah;

import com.intellij.openapi.components.ApplicationComponent;
import javax.annotation.Nonnull;

public final class HaskellLoader implements ApplicationComponent {

    @Nonnull
    public String getComponentName() {
        return "haskell.support.loader";
    }

    public void initComponent() {
        // todo
    }

    public void disposeComponent() {
    }
}
