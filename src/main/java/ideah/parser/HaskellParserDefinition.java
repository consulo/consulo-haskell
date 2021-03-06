package ideah.parser;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import consulo.lang.LanguageVersion;
import ideah.HaskellFileType;
import ideah.lexer.HaskellLexer;
import ideah.lexer.HaskellTokenTypes;
import ideah.psi.impl.HPIdentImpl;
import ideah.psi.impl.HPInfixPrefixIdentImpl;
import ideah.psi.impl.HPModuleImpl;
import ideah.psi.impl.HPOtherImpl;

public final class HaskellParserDefinition implements ParserDefinition, HaskellTokenTypes {

    public static final IFileElementType HASKELL_FILE = new IFileElementType(HaskellFileType.HASKELL_LANGUAGE);

    @Nonnull
    public Lexer createLexer(LanguageVersion languageVersion) {
        return new HaskellLexer();
    }

    public PsiParser createParser(LanguageVersion languageVersion) {
        return new HaskellParser();
    }

    public IFileElementType getFileNodeType() {
        return HASKELL_FILE;
    }

    @Nonnull
    public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
        return WHITESPACES;
    }

    @Nonnull
    public TokenSet getCommentTokens(LanguageVersion languageVersion) {
        return COMMENTS;
    }

    @Nonnull
    public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
        return STRINGS;
    }

    @Nonnull
    public PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == HaskellElementTypes.MODULE) { // where is it initialized???
            return new HPModuleImpl(node);
        } else if (type == HaskellElementTypes.INFIX_PREFIX_IDENT) {
            return new HPInfixPrefixIdentImpl(node);
        } else if (HaskellTokenTypes.IDS.contains(type)) {
            return new HPIdentImpl(node);
        } else {
            return new HPOtherImpl(node);
        }
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new HaskellFileImpl(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
