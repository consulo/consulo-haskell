package org.consulo.haskell.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootLayer;
import ideah.sdk.HaskellSdkType;

/**
 * @author VISTALL
 * @since 13.07.13.
 */
public class HaskellModuleExtension extends ModuleExtensionWithSdkImpl<HaskellModuleExtension>
{
	public HaskellModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return HaskellSdkType.class;
	}
}
