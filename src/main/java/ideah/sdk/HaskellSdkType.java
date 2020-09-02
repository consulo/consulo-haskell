package ideah.sdk;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jdom.Element;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.HashMap;
import consulo.haskell.HaskellIcons;
import consulo.ui.image.Image;
import ideah.util.ProcessLauncher;

public final class HaskellSdkType extends SdkType
{
	@Nonnull
	public static HaskellSdkType getInstance()
	{
		return EP_NAME.findExtensionOrFail(HaskellSdkType.class);
	}

	public HaskellSdkType()
	{
		super("GHC");
	}

	@Nonnull
	@Override
	public Collection<String> suggestHomePaths()
	{
		String s = suggestHomePath();
		if(s != null)
		{
			return Collections.singletonList(s);
		}
		return super.suggestHomePaths();
	}

	public String suggestHomePath()
	{
		File versionsRoot;
		String[] versions;
		String append;
		if(SystemInfo.isLinux)
		{
			versionsRoot = new File("/usr/lib");
			if(!versionsRoot.isDirectory())
			{
				return null;
			}
			versions = versionsRoot.list(new FilenameFilter()
			{
				@Override
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().startsWith("ghc") && new File(dir, name).isDirectory();
				}
			});
			append = null;
		}
		else if(SystemInfo.isWindows)
		{
			String progFiles = System.getenv("ProgramFiles(x86)");
			if(progFiles == null)
			{
				progFiles = System.getenv("ProgramFiles");
			}
			if(progFiles == null)
			{
				return null;
			}
			versionsRoot = new File(progFiles, "Haskell Platform");
			if(!versionsRoot.isDirectory())
			{
				return progFiles;
			}
			versions = versionsRoot.list();
			append = null;
		}
		else if(SystemInfo.isMac)
		{
			versionsRoot = new File("/Library/Frameworks/GHC.framework/Versions");
			if(!versionsRoot.isDirectory())
			{
				return null;
			}
			versions = versionsRoot.list();
			append = "usr";
		}
		else
		{
			return null;
		}
		String latestVersion = getLatestVersion(versions);
		if(latestVersion == null)
		{
			return null;
		}
		File versionDir = new File(versionsRoot, latestVersion);
		File homeDir;
		if(append != null)
		{
			homeDir = new File(versionDir, append);
		}
		else
		{
			homeDir = versionDir;
		}
		return homeDir.getAbsolutePath();
	}

	private static String getLatestVersion(String[] names)
	{
		if(names == null)
		{
			return null;
		}
		int length = names.length;
		if(length == 0)
		{
			return null;
		}
		if(length == 1)
		{
			return names[0];
		}
		List<GHCDir> ghcDirs = new ArrayList<GHCDir>();
		for(String name : names)
		{
			ghcDirs.add(new GHCDir(name));
		}
		Collections.sort(ghcDirs, new Comparator<GHCDir>()
		{
			@Override
			public int compare(GHCDir d1, GHCDir d2)
			{
				return d1.version.compareTo(d2.version);
			}
		});
		return ghcDirs.get(ghcDirs.size() - 1).name;
	}

	public static boolean checkForGhc(File path)
	{
		File bin = new File(path, "bin");
		if(!bin.isDirectory())
		{
			return false;
		}
		File[] children = bin.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
				{
					return false;
				}
				return "ghc".equalsIgnoreCase(FileUtil.getNameWithoutExtension(f));
			}
		});
		return children != null && children.length >= 1;
	}

	@Override
	public boolean canCreatePredefinedSdks()
	{
		return true;
	}

	@Override
	public boolean isValidSdkHome(String path)
	{
		return checkForGhc(new File(path));
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		String suggestedName;
		if(currentSdkName != null && currentSdkName.length() > 0)
		{
			suggestedName = currentSdkName;
		}
		else
		{
			String versionString = getVersionString(sdkHome);
			if(versionString != null)
			{
				suggestedName = "GHC " + versionString;
			}
			else
			{
				suggestedName = "Unknown";
			}
		}
		return suggestedName;
	}

	private final Map<String, String> cachedVersionStrings = new HashMap<String, String>();

	@Override
	public String getVersionString(String sdkHome)
	{
		if(cachedVersionStrings.containsKey(sdkHome))
		{
			return cachedVersionStrings.get(sdkHome);
		}
		String versionString = getGhcVersion(sdkHome);
		if(versionString != null && versionString.length() == 0)
		{
			versionString = null;
		}

		if(versionString != null)
		{
			cachedVersionStrings.put(sdkHome, versionString);
		}

		return versionString;
	}

	@Nullable
	public static String getGhcVersion(String homePath)
	{
		if(homePath == null || !new File(homePath).isDirectory())
		{
			return null;
		}
		try
		{
			String output = new ProcessLauncher(false, null, homePath + File.separator + "bin" + File.separator + "ghc", "--numeric-version").getStdOut();
			return output.trim();
		}
		catch(Exception ex)
		{
			// ignore
		}
		return null;
	}

	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		return new HaskellSdkConfigurable();
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData additionalData, Element additional)
	{
		if(additionalData instanceof HaskellSdkAdditionalData)
		{
			((HaskellSdkAdditionalData) additionalData).save(additional);
		}
	}

	@Override
	public SdkAdditionalData loadAdditionalData(Sdk sdk, Element additional)
	{
		return new HaskellSdkAdditionalData(additional);
	}

	@Nonnull
	@Override
	public String getPresentableName()
	{
		return "GHC";
	}

	@Override
	public Image getIcon()
	{
		return HaskellIcons.Haskell16x16;
	}
}
