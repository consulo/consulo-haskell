package ideah.run;

import javax.annotation.Nonnull;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import consulo.haskell.HaskellIcons;
import consulo.haskell.module.extension.HaskellModuleExtension;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.ui.image.Image;

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
			public boolean isApplicable(@Nonnull Project project)
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
	public Image getIcon()
	{
		return HaskellIcons.Haskell16x16;
	}

	@Override
	@Nonnull
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
