package ideah.findUsages;

import javax.annotation.Nonnull;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import ideah.lexer.HaskellLexer;
import ideah.parser.HaskellParserDefinition;
import ideah.psi.api.HPAbstractIdent;

public final class HaskellFindUsagesProvider implements FindUsagesProvider {

    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new HaskellLexer(),
            HaskellParserDefinition.IDS, HaskellParserDefinition.COMMENTS, HaskellParserDefinition.STRINGS);
    }

    public boolean canFindUsagesFor(@Nonnull PsiElement psiElement) {
        return psiElement instanceof HPAbstractIdent;
    }

    public String getHelpId(@Nonnull PsiElement psiElement) {
        return null;
    }

    @Nonnull
    public String getType(@Nonnull PsiElement psiElement) {
        return "symbol";
    }

    @Nonnull
    public String getDescriptiveName(@Nonnull PsiElement psiElement) {
        return psiElement.getText();
    }

    @Nonnull
    public String getNodeText(@Nonnull PsiElement element, boolean useFullName) {
        if (element instanceof HPAbstractIdent) {
            HPAbstractIdent ident = (HPAbstractIdent) element;
            String name = ident.getName();
            return name == null ? ident.getText() : name;
        }
        return element.getText();
    }
}
