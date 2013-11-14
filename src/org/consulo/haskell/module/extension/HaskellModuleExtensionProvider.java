package org.consulo.haskell.module.extension;

import com.intellij.openapi.module.Module;
import ideah.HaskellFileType;
import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author VISTALL
 * @since 13.07.13.
 */
public class HaskellModuleExtensionProvider implements ModuleExtensionProvider<HaskellModuleExtension, HaskellMutableModuleExtension> {
	@Nullable
	@Override
	public Icon getIcon() {
		return HaskellFileType.HASKELL_ICON;
	}

	@NotNull
	@Override
	public String getName() {
		return "Haskell";
	}

	@NotNull
	@Override
	public Class<HaskellModuleExtension> getImmutableClass() {
		return HaskellModuleExtension.class;
	}

	@NotNull
	@Override
	public HaskellModuleExtension createImmutable(@NotNull String s, @NotNull Module module) {
		return new HaskellModuleExtension(s, module);
	}

	@NotNull
	@Override
	public HaskellMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull HaskellModuleExtension moduleExtension) {
		return new HaskellMutableModuleExtension(s, module, moduleExtension);
	}
}
