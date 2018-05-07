package ideah.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import ideah.psi.api.HaskellPsiElement;

public abstract class HaskellBaseElementImpl extends ASTDelegatePsiElement implements HaskellPsiElement {

    @Nonnull
    private final ASTNode node;

    protected HaskellBaseElementImpl(@Nonnull ASTNode node) {
        this.node = node;
    }

    public final PsiElement getParent() {
        return SharedImplUtil.getParent(getNode());
    }

    @Nonnull
    @Override
    public final ASTNode getNode() {
        return node;
    }

    protected final String getSrcSpan() {
        return node.getTextRange().toString();
    }

    @Override
    public String toString() {
        return node.getText();
    }
}
