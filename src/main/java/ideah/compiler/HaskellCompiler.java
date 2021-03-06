package ideah.compiler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import consulo.compiler.impl.resourceCompiler.ResourceCompilerConfiguration;
import consulo.haskell.module.extension.HaskellModuleExtension;
import ideah.HaskellFileType;
import ideah.sdk.HaskellSdkType;
import ideah.util.CompilerLocation;
import ideah.util.DeclarationPosition;
import ideah.util.LineCol;

public final class HaskellCompiler implements TranslatingCompiler
{

	private final Project project;

	public HaskellCompiler(Project project)
	{
		this.project = project;
	}

	public boolean isCompilableFile(VirtualFile file, CompileContext context)
	{
		return isCompilableFile(file);
	}

	public static boolean isCompilableFile(VirtualFile file)
	{
		FileType fileType = FileTypeManager.getInstance().getFileTypeByFile(file);
		return HaskellFileType.INSTANCE.equals(fileType);
	}

	public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink)
	{
		Map<Module, List<VirtualFile>> mapModulesToVirtualFiles;
		if(moduleChunk.getNodes().size() == 1)
		{
			mapModulesToVirtualFiles = Collections.singletonMap(moduleChunk.getNodes().iterator().next(), Arrays.asList(files));
		}
		else
		{
			mapModulesToVirtualFiles = CompilerUtil.buildModuleToFilesMap(context, files);
		}
		for(Module module : moduleChunk.getNodes())
		{
			List<VirtualFile> moduleFiles = mapModulesToVirtualFiles.get(module);
			if(moduleFiles == null)
			{
				continue;
			}

			ModuleFileIndex index = ModuleRootManager.getInstance(module).getFileIndex();
			List<VirtualFile> toCompile = new ArrayList<VirtualFile>();
			List<VirtualFile> toCompileTests = new ArrayList<VirtualFile>();

			if(isAcceptableModuleType(module))
			{
				for(VirtualFile file : moduleFiles)
				{
					if(shouldCompile(file, project))
					{
						(index.isInTestSourceContent(file) ? toCompileTests : toCompile).add(file);
					}
				}
			}

			if(!toCompile.isEmpty())
			{
				compileFiles(context, module, toCompile, sink, false);
			}
			if(!toCompileTests.isEmpty())
			{
				compileFiles(context, module, toCompileTests, sink, true);
			}
		}
	}

	@Nonnull
	@Override
	public FileType[] getInputFileTypes()
	{
		return new FileType[]{HaskellFileType.INSTANCE};
	}

	@Nonnull
	@Override
	public FileType[] getOutputFileTypes()
	{
		return new FileType[]{HiFileType.INSTANCE};
	}

	static VirtualFile getMainOutput(CompileContext compileContext, Module module, boolean tests)
	{
		return tests ? compileContext.getModuleOutputDirectoryForTests(module) : compileContext.getModuleOutputDirectory(module);
	}

	private static void compileFiles(CompileContext context, Module module, List<VirtualFile> toCompile, OutputSink sink, boolean tests)
	{
		if(CompilerLocation.get(module) == null)
		{
			return; // todo: produce error
		}
		VirtualFile outputDir = getMainOutput(context, module, tests);
		List<OutputItem> output = new ArrayList<OutputItem>();
		// todo: pass all files to compiler at once (more effective?)
		for(VirtualFile file : toCompile)
		{
			for(GHCMessage message : LaunchGHC.compile(outputDir, file.getPath(), module, tests))
			{
				VirtualFile errFile = LocalFileSystem.getInstance().findFileByPath(message.getFileName());
				String url = errFile == null ? message.getFileName() : errFile.getUrl();
				LineCol coord = message.getRange().start;
				context.addMessage(message.getCategory(), message.getErrorMessage(), url, coord.line, coord.column);
			}
		}
		sink.add(outputDir.getPath(), output, VfsUtil.toVirtualFileArray(toCompile));
	}

	private static boolean shouldCompile(VirtualFile file, Project project)
	{
		ResourceCompilerConfiguration c = ResourceCompilerConfiguration.getInstance(project);
		return !c.isResourceFile(file);
	}

	private static boolean isAcceptableModuleType(Module module)
	{
		return ModuleUtilCore.getExtension(module, HaskellModuleExtension.class) != null;
	}

	@Nonnull
	public String getDescription()
	{
		return "Haskell compiler";
	}

	public boolean validateConfiguration(CompileScope compileScope)
	{
		VirtualFile[] files = compileScope.getFiles(HaskellFileType.INSTANCE, true);
		if(files.length == 0)
		{
			return true;
		}

		Set<Module> modules = new HashSet<Module>();
		for(VirtualFile file : files)
		{
			Module module = DeclarationPosition.getModule(project, file);
			if(module != null)
			{
				modules.add(module);
			}
		}

		Set<Module> noGhcModules = new HashSet<Module>();
		for(Module module : modules)
		{
			if(!isAcceptableModuleType(module))
			{
				continue;
			}
			Sdk sdk = ModuleUtilCore.getSdk(module, HaskellModuleExtension.class);
			if(sdk == null || !(sdk.getSdkType() instanceof HaskellSdkType))
			{
				noGhcModules.add(module);
			}
		}

		if(!noGhcModules.isEmpty())
		{
			if(noGhcModules.size() == 1)
			{
				Module module = noGhcModules.iterator().next();
				Messages.showErrorDialog(project, MessageFormat.format("Cannot compile Haskell files.\nPlease set up GHC for module ''{0}''.", module.getName()), "Cannot Compile");
			}
			else
			{
				StringBuilder buf = new StringBuilder();
				int i = 0;
				for(Module module : noGhcModules)
				{
					if(i > 0)
					{
						buf.append(", ");
					}
					buf.append(module.getName());
					i++;
				}
				Messages.showErrorDialog(project, MessageFormat.format("Cannot compile Haskell files.\nPlease set up GHC for modules ''{0}''.", buf.toString()), "Cannot Compile");
			}
			return false;
		}

		return true;
	}
}
