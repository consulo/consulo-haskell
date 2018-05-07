package ideah.psi.api.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class HaskellPsiElementFactory {

    public static HaskellPsiElementFactory getInstance(Project project) {
        return ServiceManager.getService(project, HaskellPsiElementFactory.class);
    }

    @Nullable
    public abstract ASTNode createIdentNodeFromText(@Nonnull String newName);
}
