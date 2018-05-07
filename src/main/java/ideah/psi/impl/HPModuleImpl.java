package ideah.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import ideah.psi.api.HPModule;

public final class HPModuleImpl extends HaskellBaseElementImpl implements HPModule {

    public HPModuleImpl(@Nonnull ASTNode node) {
        super(node);
    }
}
