package consulo.haskell.module.extension;

import javax.annotation.Nonnull;

import com.intellij.openapi.projectRoots.SdkType;
import consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import consulo.roots.ModuleRootLayer;
import ideah.sdk.HaskellSdkType;

/**
 * @author VISTALL
 * @since 13.07.13.
 */
public class HaskellModuleExtension extends ModuleExtensionWithSdkImpl<HaskellModuleExtension>
{
	public HaskellModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@Nonnull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return HaskellSdkType.class;
	}
}
