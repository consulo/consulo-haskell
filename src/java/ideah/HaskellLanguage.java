package ideah;

import com.intellij.lang.Language;

public final class HaskellLanguage extends Language {
	public static final HaskellLanguage INSTANCE = new HaskellLanguage();

	public HaskellLanguage() {
		super("Haskell");
	}

	@Override
	public boolean isCaseSensitive() {
		return true;
	}
}
