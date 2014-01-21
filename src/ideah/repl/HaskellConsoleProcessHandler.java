package ideah.repl;

import java.util.regex.Matcher;

import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import ideah.HaskellFileType;

public final class HaskellConsoleProcessHandler extends ColoredProcessHandler
{

	private final LanguageConsoleImpl myConsole;

	HaskellConsoleProcessHandler(Process process, String commandLine, LanguageConsoleImpl console)
	{
		super(process, commandLine, HaskellFileType.HASKELL_CHARSET);
		this.myConsole = console;
	}

	@Override
	public void coloredTextAvailable(String text, Key attributes)
	{
		String string = processPrompts(myConsole, StringUtil.convertLineSeparators(text));
		myConsole.printToHistory(string, ConsoleViewContentType.getConsoleViewType(attributes).getAttributes());
	}

	private static String processPrompts(LanguageConsoleImpl console, String text)
	{
		if(text != null && text.matches(HaskellConsoleHighlightingUtil.LINE_WITH_PROMPT))
		{
			Matcher matcher = HaskellConsoleHighlightingUtil.GHCI_PATTERN.matcher(text);
			matcher.find();
			String prefix = matcher.group();
			String trimmed = StringUtil.trimStart(text, prefix).trim();
			console.setPrompt(prefix + " ");
			return trimmed;
		}
		return text;
	}

	public LanguageConsoleImpl getLanguageConsole()
	{
		return myConsole;
	}
}
