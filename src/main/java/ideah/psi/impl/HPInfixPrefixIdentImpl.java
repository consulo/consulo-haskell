package ideah.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import ideah.psi.api.HPInfixPrefixIdent;

public final class HPInfixPrefixIdentImpl extends HaskellAbstractIdentImpl implements HPInfixPrefixIdent {

    public HPInfixPrefixIdentImpl(@Nonnull ASTNode node) {
        super(node);
    }

    @Override
    protected ASTNode getNodeToBeReplaced() {
        return getNode();
    }

    @Override
    protected ASTNode getNodeToBeInsertedTo() {
        return getParent().getNode();
    }

    @Override
    protected boolean isPrefixInfixIdent() {
        return true;
    }
}
