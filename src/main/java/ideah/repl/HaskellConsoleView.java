package ideah.repl;

import javax.annotation.Nonnull;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.project.Project;
import ideah.HaskellLanguage;

public final class HaskellConsoleView extends LanguageConsoleImpl
{
	private HaskellConsoleExecuteActionHandler myExecuteHandler;

	public HaskellConsoleView(@Nonnull Project project, @Nonnull String title)
	{
		super(project, title, HaskellLanguage.INSTANCE);
	}

	public void setExecuteHandler(HaskellConsoleExecuteActionHandler executeHandler)
	{
		myExecuteHandler = executeHandler;
	}

	public HaskellConsoleExecuteActionHandler getExecuteHandler()
	{
		return myExecuteHandler;
	}
}
