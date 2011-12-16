package ideah.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public final class HaddockLocation {

    private static final Logger LOG = Logger.getInstance("ideah.util.HaddockLocation");
    private static final String MAIN_FILE = "ask_haddock";

    public final String exe;

    private HaddockLocation(String exe) {
        this.exe = exe;
    }

    private static boolean equalVersion(@NotNull List<Integer> v1, @NotNull List<Integer> v2) {
        int size = v1.size();
        if (size != v2.size())
            return false;
        for (int i = 0; i < size; i++) {
            if (!v1.get(i).equals(v2.get(i)))
                return false;
        }
        return true;
    }

    private static List<String> getMissingPackages(@NotNull String cabalPath, String... packages) throws IOException, InterruptedException {
        List<String> args = new ArrayList<String>();
        args.addAll(Arrays.asList(cabalPath, "list", "--installed", "-v0", "--simple-output"));
        TreeMap<String, String> argsPackages = new TreeMap<String, String>();
        for (String pkg : packages) {
            argsPackages.put(pkg, pkg.substring(0, pkg.lastIndexOf('-')));
        }
        args.addAll(argsPackages.values()); // For "haddock list --installed <package name>", the package name should not include the version
        List<String> packageList = new ArrayList<String>(Arrays.asList(packages));
        Collections.sort(packageList);
        List<String> missingPackages = new ArrayList<String>();
        Iterator<String> iterator = packageList.iterator();
        ProcessLauncher getMissingPackagesLauncher = new ProcessLauncher(true, null, args);
        BufferedReader reader = new BufferedReader(new StringReader(getMissingPackagesLauncher.getStdOut()));
        String line = reader.readLine();
        while (line != null && iterator.hasNext()) {
            String next = iterator.next();
            if (line.startsWith(argsPackages.get(next)) && equalVersion(GHCUtil.getVersion(next), GHCUtil.getVersion(line))) {
                line = reader.readLine();
            } else {
                missingPackages.add(next);
            }
        }
        while (iterator.hasNext()) {
            missingPackages.add(iterator.next());
        }
        return missingPackages;
    }

    private static void runCabal(@NotNull String cabalPath, List<String> cabalArgsList) throws IOException, InterruptedException {
        Process cabalProcess = Runtime.getRuntime().exec(cabalPath, cabalArgsList.toArray(new String[cabalArgsList.size()]));
        cabalProcess.waitFor();
    }

    private static void cabalInstall(@NotNull String cabalPath, @Nullable ProgressIndicator indicator, @NotNull List<String> packages) throws IOException, InterruptedException {
        if (packages.isEmpty())
            return;
        if (indicator != null) {
            StringBuilder buf = new StringBuilder("Installing missing package" + (packages.size() > 1 ? "s" : "") + " (");
            for (int i = 0; i < packages.size(); i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(packages.get(i));
            }
            buf.append(")...");
            indicator.setText(buf.toString());
        }
        runCabal(cabalPath, Arrays.asList("update"));
        if (indicator == null) {
            List<String> cabalArgsList = new ArrayList<String>();
            cabalArgsList.add("install");
            cabalArgsList.addAll(packages);
            runCabal(cabalPath, cabalArgsList);
        } else {
            double progress = 1.0 / packages.size();
            for (String pkg : packages) {
                runCabal(cabalPath, Arrays.asList("install", pkg));
                indicator.setFraction(indicator.getFraction() + progress);
            }
        }
    }

    // todo: HTTP proxy settings
    private static void cabalCheckAndInstall(@NotNull String cabalPath, @Nullable ProgressIndicator indicator, String... packages) {
        if (packages.length > 0) {
            try {
                List<String> missingPackages = getMissingPackages(cabalPath, packages);
                cabalInstall(cabalPath, indicator, missingPackages);
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    public static synchronized HaddockLocation get(@Nullable Module module, @Nullable ProgressIndicator indicator) {
        AskUtil ask = AskUtil.get(module, MAIN_FILE);
        String cabalPath = ask.getCabalPath();
        if (cabalPath == null)
            return null;
        try {
            if (ask.needRecompile()) {
                cabalCheckAndInstall(cabalPath, indicator, "haddock-2.9.2", "uu-parsinglib-2.7.3");
                if (!ask.compileHs())
                    return null;
            }
            File exe = ask.getExe();
            if (exe != null) {
                return new HaddockLocation(exe.getAbsolutePath());
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.error(ex);
            return null;
        }
    }
}
