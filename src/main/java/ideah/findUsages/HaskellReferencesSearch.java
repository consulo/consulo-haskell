package ideah.findUsages;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import ideah.compiler.HaskellCompiler;
import ideah.psi.api.HPAbstractIdent;
import ideah.psi.impl.HPIdentImpl;
import ideah.util.*;
import javax.annotation.Nonnull;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class HaskellReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    private static final Logger LOG = Logger.getInstance("ideah.findUsages.HaskellReferencesSearch");

    public HaskellReferencesSearch() {
        super(true);
    }

    @Override
    public void processQuery(@Nonnull ReferencesSearch.SearchParameters queryParameters, @Nonnull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        if (!(element instanceof HPAbstractIdent))
            return;
        PsiFile file = element.getContainingFile();
        try {
            DeclarationPosition declaration = DeclarationPosition.get(file, LineCol.fromOffset(file, element.getTextOffset()));
            if (declaration == null)
                return;
            LineCol coord = declaration.coord;
            VirtualFile virtualFile = file.getVirtualFile();
            Project project = file.getProject();
            ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
            if (virtualFile == null)
                return;
            Module module = fileIndex.getModuleForFile(virtualFile);
            CompilerLocation compiler = CompilerLocation.get(module);
            if (compiler == null)
                return;
            List<String> args = compiler.getCompileOptionsList(
                "-m", "FindUsages",
                "-s", GHCUtil.rootsAsString(module, false),
                "--line-number", String.valueOf(coord.line), "--column-number", String.valueOf(coord.column),
                "-f", virtualFile.getPath()
            );
            final List<String> srcFiles = new ArrayList<String>();
            fileIndex.iterateContent(new ContentIterator() {
                public boolean processFile(VirtualFile virtualFile) {
                    if (HaskellCompiler.isCompilableFile(virtualFile)) {
                        srcFiles.add(virtualFile.getPath());
                    }
                    return true;
                }
            });
            args.addAll(srcFiles);
            ProcessLauncher launcher = new ProcessLauncher(true, null, args);
            BufferedReader bf = new BufferedReader(new StringReader(launcher.getStdOut()));
            while (true) {
                String srcLineCol = bf.readLine();
                if (srcLineCol == null)
                    break;
                LineCol refLineCol = LineCol.parse(srcLineCol);
                String srcModule = bf.readLine();
                if (srcModule != null) {
                    PsiElement elementAt = HPIdentImpl.getElementAt(project, new DeclarationPosition(refLineCol, srcModule));
                    PsiReference reference = elementAt.getReference();
                    consumer.process(reference);
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
