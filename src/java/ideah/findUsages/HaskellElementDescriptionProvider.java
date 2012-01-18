package ideah.findUsages;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import ideah.psi.api.HPIdent;
import org.jetbrains.annotations.NotNull;

public final class HaskellElementDescriptionProvider implements ElementDescriptionProvider {

    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        if (element instanceof HPIdent && location instanceof UsageViewLongNameLocation) {
            return element.getText();
        }
        return null;
    }
}
