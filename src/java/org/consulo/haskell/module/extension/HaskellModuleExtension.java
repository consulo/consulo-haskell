package org.consulo.haskell.module.extension;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.SdkType;
import ideah.sdk.HaskellSdkType;
import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 13.07.13.
 */
public class HaskellModuleExtension extends ModuleExtensionWithSdkImpl<HaskellModuleExtension> {
	public HaskellModuleExtension(@NotNull String id, @NotNull Module module) {
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass() {
		return HaskellSdkType.class;
	}
}
