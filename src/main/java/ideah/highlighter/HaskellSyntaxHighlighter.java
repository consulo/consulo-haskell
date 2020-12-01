package ideah.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import consulo.ui.color.ColorValue;
import consulo.ui.color.RGBColor;
import consulo.ui.style.StandardColors;
import ideah.lexer.HaskellLexer;
import ideah.lexer.HaskellTokenTypes;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class HaskellSyntaxHighlighter extends SyntaxHighlighterBase implements HaskellTokenTypes
{

	private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

	@NonNls
	static final String STRING_ID = "String";
	@NonNls
	static final String NUMBER_ID = "Number";
	@NonNls
	static final String COMMENT_ID = "Line comment";
	@NonNls
	static final String ML_COMMENT_ID = "Block comment";
	@NonNls
	static final String KEYWORD_ID = "Keyword";
	@NonNls
	static final String KEY_SYM_ID = "Special symbol";
	@NonNls
	static final String TYCON_ID = "Type or constructor";
	@NonNls
	static final String SYM_ID = "Operator or identifier";
	@NonNls
	static final String STD_FUNCTION_ID = "Standard function";
	@NonNls
	static final String PAREN_ID = "Parenteses";
	@NonNls
	static final String BRACKETS_ID = "Brackets";
	@NonNls
	static final String BRACES_ID = "Braces";
	@NonNls
	static final String ERROR_STRING_ID = "Wrong string";
	@NonNls
	static final String ERROR_NUMBER_ID = "Wrong number";
	@NonNls
	static final String ERROR_UNDEFINED_ID = "Wrong token";

	private static TextAttributes modify(TextAttributesKey key, ColorValue color)
	{
		TextAttributes attrs = key.getDefaultAttributes().clone();
		attrs.setForegroundColor(color);
		return attrs;
	}

	private static TextAttributes modify(TextAttributesKey key, ColorValue color, int fontType)
	{
		TextAttributes attrs = modify(key, color);
		attrs.setFontType(fontType);
		return attrs;
	}

	static final TextAttributesKey STRING_ATTR = TextAttributesKey.createTextAttributesKey(STRING_ID, DefaultLanguageHighlighterColors.STRING);
	static final TextAttributesKey NUMBER_ATTR = TextAttributesKey.createTextAttributesKey(NUMBER_ID, DefaultLanguageHighlighterColors.NUMBER);
	static final TextAttributesKey LINE_COMMENT_ATTR = TextAttributesKey.createTextAttributesKey(COMMENT_ID, DefaultLanguageHighlighterColors.LINE_COMMENT);
	static final TextAttributesKey ML_COMMENT_ATTR = TextAttributesKey.createTextAttributesKey(ML_COMMENT_ID, DefaultLanguageHighlighterColors.BLOCK_COMMENT);
	static final TextAttributesKey KEYWORD_ATTR = TextAttributesKey.createTextAttributesKey(KEYWORD_ID, DefaultLanguageHighlighterColors.KEYWORD);
	static final TextAttributesKey KEYSYM_ATTR = TextAttributesKey.createTextAttributesKey(KEY_SYM_ID, DefaultLanguageHighlighterColors.KEYWORD // todo: ???
	);
	static final TextAttributesKey CON_ATTR = TextAttributesKey.createTextAttributesKey(TYCON_ID, modify(DefaultLanguageHighlighterColors.KEYWORD, new RGBColor(38, 139, 211)) // todo: ???
	);
	static final TextAttributesKey SYM_ATTR = TextAttributesKey.createTextAttributesKey(SYM_ID, DefaultLanguageHighlighterColors.OPERATION_SIGN // todo: ???
	);
	static final TextAttributesKey STD_FUNCTION_ATTR = TextAttributesKey.createTextAttributesKey(STD_FUNCTION_ID, modify(DefaultLanguageHighlighterColors.KEYWORD, StandardColors.BLACK, Font.BOLD | Font
			.ITALIC));
	static final TextAttributesKey PAREN_ATTR = TextAttributesKey.createTextAttributesKey(PAREN_ID, DefaultLanguageHighlighterColors.PARENTHESES);
	static final TextAttributesKey BRACES_ATTR = TextAttributesKey.createTextAttributesKey(BRACES_ID, DefaultLanguageHighlighterColors.BRACES);
	static final TextAttributesKey BRACKETS_ATTR = TextAttributesKey.createTextAttributesKey(BRACKETS_ID, DefaultLanguageHighlighterColors.BRACKETS);
	static final TextAttributesKey ERROR_STRING_ATTR = TextAttributesKey.createTextAttributesKey(ERROR_STRING_ID, modify(DefaultLanguageHighlighterColors.STRING, StandardColors.RED));
	static final TextAttributesKey ERROR_NUMBER_ATTR = TextAttributesKey.createTextAttributesKey(ERROR_NUMBER_ID, modify(DefaultLanguageHighlighterColors.NUMBER, StandardColors.RED));
	static final TextAttributesKey ERROR_UNDEFINED_ATTR = TextAttributesKey.createTextAttributesKey(ERROR_UNDEFINED_ID, HighlighterColors.BAD_CHARACTER);

	private static final TokenSet TS_STRING = TokenSet.create(STRING, CHAR);
	private static final TokenSet TS_NUMBER = TokenSet.create(INTEGER, OCTAL, HEX, FLOAT);
	private static final TokenSet TS_LINE_COMMENT = TokenSet.create(COMMENT);
	private static final TokenSet TS_ML_COMMENT = TokenSet.create(ML_COMMENT);
	private static final TokenSet TS_KEYWORD = TokenSet.create(KEYWORD);
	private static final TokenSet TS_KEYSYM = TokenSet.create(KEY_OP, SPECIAL, COMMA);
	private static final TokenSet TS_CON = TokenSet.create(CON_ID);
	private static final TokenSet TS_SYM = TokenSet.create(VAR_SYM, CON_SYM);
	private static final TokenSet TS_STD_FUNCTION = TokenSet.create(STD_FUNCTION);
	private static final TokenSet TS_PAREN = TokenSet.create(L_PAREN, R_PAREN);
	private static final TokenSet TS_BRACKETS = TokenSet.create(L_SQUARE, R_SQUARE);
	private static final TokenSet TS_BRACES = TokenSet.create(L_CURLY, R_CURLY);
	private static final TokenSet TS_ERROR_STRING = TokenSet.create(ERROR_STRING);
	private static final TokenSet TS_ERROR_NUMBER = TokenSet.create(ERROR_NUMBER);
	private static final TokenSet TS_ERROR_UNDEFINED = TokenSet.create(ERROR_UNDEFINED);

	static
	{
		fillMap(ATTRIBUTES, TS_STRING, STRING_ATTR);
		fillMap(ATTRIBUTES, TS_NUMBER, NUMBER_ATTR);
		fillMap(ATTRIBUTES, TS_LINE_COMMENT, LINE_COMMENT_ATTR);
		fillMap(ATTRIBUTES, TS_ML_COMMENT, ML_COMMENT_ATTR);
		fillMap(ATTRIBUTES, TS_KEYWORD, KEYWORD_ATTR);
		fillMap(ATTRIBUTES, TS_KEYSYM, KEYSYM_ATTR);
		fillMap(ATTRIBUTES, TS_CON, CON_ATTR);
		fillMap(ATTRIBUTES, TS_SYM, SYM_ATTR);
		fillMap(ATTRIBUTES, TS_STD_FUNCTION, STD_FUNCTION_ATTR);
		fillMap(ATTRIBUTES, TS_PAREN, PAREN_ATTR);
		fillMap(ATTRIBUTES, TS_BRACKETS, BRACKETS_ATTR);
		fillMap(ATTRIBUTES, TS_BRACES, BRACES_ATTR);
		fillMap(ATTRIBUTES, TS_ERROR_STRING, ERROR_STRING_ATTR);
		fillMap(ATTRIBUTES, TS_ERROR_NUMBER, ERROR_NUMBER_ATTR);
		fillMap(ATTRIBUTES, TS_ERROR_UNDEFINED, ERROR_UNDEFINED_ATTR);
	}

	@Nonnull
	public Lexer getHighlightingLexer()
	{
		return new HaskellLexer();
	}

	@Nonnull
	public TextAttributesKey[] getTokenHighlights(IElementType type)
	{
		return pack(ATTRIBUTES.get(type));
	}
}
