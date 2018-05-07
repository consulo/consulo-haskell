package ideah.run;

import javax.annotation.Nonnull;

import com.intellij.execution.configurations.SimpleProgramParameters;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.haskell.module.extension.HaskellModuleExtension;

final class HaskellParameters extends SimpleProgramParameters {

    private Sdk ghc;
    private String mainFile;
    private String rtFlags;

    public Sdk getGhc() {
        return ghc;
    }

    public void setGhc(Sdk ghc) {
        this.ghc = ghc;
    }

    public String getMainFile() {
        return mainFile;
    }

    public void setMainFile(String mainFile) {
        this.mainFile = mainFile;
    }

    public String getRuntimeFlags() {
        return rtFlags;
    }

    public void setRuntimeFlags(String rtFlags) {
        this.rtFlags = rtFlags;
    }

    public void configureByModule(@Nonnull Module module) {
        setGhc(getModuleGhc(module));
    }

    public static Sdk getModuleGhc(@Nonnull Module module) {
        Sdk ghc = ModuleUtilCore.getSdk(module, HaskellModuleExtension.class);
        if (ghc == null)
            return null;
        VirtualFile homeDirectory = ghc.getHomeDirectory();
        if (homeDirectory == null || !homeDirectory.isValid())
            return null;
        return ghc;
    }
}
