package haskell.compiler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.VirtualFile;
import haskell.util.ProcessLauncher;

import java.io.*;
import java.util.*;

public final class LaunchGHC {

    private static final Logger LOG = Logger.getInstance("haskell.compiler.LaunchGHC");

    static final String EOLN = "\n";
    private static final String ERR_TEST = "err_test";

    private static String getErrTestExe(Module module) throws IOException, InterruptedException {
        File pluginPath = new File(System.getProperty("user.home"), ".ideah");
        pluginPath.mkdirs();
        File errTestExe = new File(pluginPath, getExeName(ERR_TEST));
        String hsName = getHsName(ERR_TEST);
        if (errTestExe.exists()) {
            File errTestHs = new File(pluginPath, hsName);
            if (errTestHs.exists()) {
                Date exeDate = new Date(errTestExe.toURI().toURL().openConnection().getLastModified());
                Date hsDate = new Date(errTestHs.toURI().toURL().openConnection().getLastModified());
                if (hsDate.after(exeDate)) {
                    compileHs(module, pluginPath, hsName);
                }
            }
        } else {
            compileHs(module, pluginPath, hsName);
        }

        return errTestExe.getAbsolutePath();
    }

    private static void compileHs(Module module, File pluginPath, String hsName) throws IOException, InterruptedException {
        Class<?> cls = LaunchGHC.class;
        File oneOfHsPluginFiles = new File(cls.getResource("/haskell/" + hsName).getPath());
        File pluginHaskellDir = new File(oneOfHsPluginFiles.getParent());
        File[] haskellDirFiles = pluginHaskellDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".hs");
            }
        });
        for (File file : haskellDirFiles) {
            InputStream is = new FileInputStream(file);
            try {
                OutputStream os = new FileOutputStream(new File(pluginPath, file.getName()));
                try {
                    StreamUtil.copyStreamContent(is, os);
                } finally {
                    os.close();
                }
            } finally {
                is.close();
            }
        }
        String absolutePluginPath = pluginPath.getAbsolutePath();
        String separator = System.getProperty("file.separator");
        List<String> args = new ArrayList<String>();
        args.addAll(Arrays.asList(getBinVFile(module).getPath() + "/ghc.exe",
                "--make", "-package", "ghc",
                "-i" + absolutePluginPath,
                absolutePluginPath + separator + hsName));
        ProcessBuilder pb = new ProcessBuilder(args);
        Process p = pb.start();
        p.waitFor();
    }

    private static String getExeName(String errTest) {
        return errTest + ".exe";
    }

    private static String getHsName(String errTest) {
        return errTest + ".hs";
    }


    public static List<GHCMessage> getGHCMessages(VirtualFile output, String fileName, Module module, boolean tests) {
        try {
            VirtualFile libPath = getLibVFile(module);
            if (libPath == null)
                return Collections.emptyList();
            List<String> args = new ArrayList<String>();
            args.add(getErrTestExe(module));
            VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(tests);
            args.addAll(Arrays.asList(
                "-g", libPath.getPath(),
                "-c", "-W",
                "-s", rootsToString(sourceRoots)
            ));
            if (output != null) {
                args.addAll(Arrays.asList(
                    "-o", output.getPath()
                ));
            }
            args.add(fileName);
            ProcessLauncher launcher = new ProcessLauncher(args);
            String stdOut = launcher.getStdOut();
            return parseMessages(stdOut);
        } catch (Exception ex) {
            LOG.error(ex);
            return Collections.singletonList(new GHCMessage(ex.toString(), fileName));
        }
    }

    private static String rootsToString(VirtualFile[] roots) {
        StringBuilder sourceRoots = new StringBuilder();
        for (VirtualFile root : roots) {
            sourceRoots.append(":").append(root.getPath());
        }
        return sourceRoots.substring(1);
    }

    private static List<GHCMessage> parseMessages(String output) throws IOException {
        List<StringBuffer> buffers = new ArrayList<StringBuffer>();
        List<GHCMessage> ghcMessages = new ArrayList<GHCMessage>();
        BufferedReader ghcErrorReader = new BufferedReader(new StringReader(output));
        StringBuffer tmpBuffer = new StringBuffer();
        String line = ghcErrorReader.readLine();
        while (line != null) {
            if (line.startsWith("\f")) {
                tmpBuffer = new StringBuffer();
                buffers.add(tmpBuffer);
            } else {
                tmpBuffer.append(line).append(EOLN);
            }
            line = ghcErrorReader.readLine();
        }
        for (StringBuffer buffer : buffers) {
            ghcMessages.add(new GHCMessage(buffer.toString()));
        }
        return ghcMessages;
    }

    public static VirtualFile getLibVFile(Module module) {
        return getSomeVFile(module, "lib");
    }

    public static VirtualFile getBinVFile(Module module) {
        return getSomeVFile(module, "bin");
    }

    private static VirtualFile getSomeVFile(Module module, String dirName) {
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        if (sdk == null)
            return null;
        VirtualFile sdkHome = sdk.getHomeDirectory();
        if (sdkHome == null)
            return null;
        return sdkHome.findChild(dirName);
    }
}
