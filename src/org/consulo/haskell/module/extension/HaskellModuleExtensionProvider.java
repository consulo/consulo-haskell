package org.consulo.haskell.module.extension;

import javax.swing.Icon;

import org.consulo.haskell.HaskellIcons;
import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 13.07.13.
 */
public class HaskellModuleExtensionProvider implements ModuleExtensionProvider<HaskellModuleExtension, HaskellMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return HaskellIcons.Haskell16x16;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Haskell";
	}

	@NotNull
	@Override
	public HaskellModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new HaskellModuleExtension(s, module);
	}

	@NotNull
	@Override
	public HaskellMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module)
	{
		return new HaskellMutableModuleExtension(s, module);
	}
}
