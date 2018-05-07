package ideah.findUsages;

import javax.annotation.Nonnull;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import ideah.psi.api.HPAbstractIdent;

public final class HaskellElementDescriptionProvider implements ElementDescriptionProvider {

    public String getElementDescription(@Nonnull PsiElement element, @Nonnull ElementDescriptionLocation location) {
        if (element instanceof HPAbstractIdent && location instanceof UsageViewLongNameLocation) {
            return element.getText();
        }
        return null;
    }
}
