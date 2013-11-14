package ideah.run;

import javax.swing.Icon;

import org.consulo.haskell.HaskellIcons;
import org.consulo.haskell.module.extension.HaskellModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.module.extension.ModuleExtensionHelper;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public final class HaskellRunConfigurationType implements ConfigurationType
{
	public static HaskellRunConfigurationType getInstance()
	{
		return CONFIGURATION_TYPE_EP.findExtension(HaskellRunConfigurationType.class);
	}

	private final ConfigurationFactory myFactory;

	public HaskellRunConfigurationType()
	{
		this.myFactory = new ConfigurationFactoryEx(this)
		{
			@Override
			public boolean isApplicable(@NotNull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(HaskellModuleExtension.class);
			}

			@Override
			public RunConfiguration createTemplateConfiguration(Project project)
			{
				return new HaskellRunConfiguration(project, this);
			}
		};
	}

	@Override
	public String getDisplayName()
	{
		return "Haskell";
	}

	@Override
	public String getConfigurationTypeDescription()
	{
		return "Haskell application";
	}

	@Override
	public Icon getIcon()
	{
		return HaskellIcons.Haskell16x16;
	}

	@Override
	@NotNull
	public String getId()
	{
		return "HaskellRunConfiguration";
	}

	@Override
	public ConfigurationFactory[] getConfigurationFactories()
	{
		return new ConfigurationFactory[]{myFactory};
	}
}
