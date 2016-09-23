package ideah.repl;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.openapi.project.Project;
import ideah.HaskellLanguage;

public final class HaskellConsoleView extends LanguageConsoleImpl
{
	private HaskellConsoleExecuteActionHandler myExecuteHandler;

	public HaskellConsoleView(@NotNull Project project, @NotNull String title)
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
