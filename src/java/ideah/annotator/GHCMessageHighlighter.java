package ideah.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import ideah.compiler.GHCMessage;
import ideah.compiler.LaunchGHC;
import ideah.util.DeclarationPosition;
import ideah.util.LineColRange;

import java.io.File;
import java.util.List;

public final class GHCMessageHighlighter implements ExternalAnnotator {

    public void annotate(PsiFile psiFile, AnnotationHolder annotationHolder) {
        VirtualFile file = psiFile.getVirtualFile();
        if (file == null)
            return;
        Module module = DeclarationPosition.getDeclModule(psiFile);
        List<GHCMessage> ghcMessages = LaunchGHC.compileAndGetGhcMessages(null, file.getPath(), module, true);
        File mainFile = new File(file.getPath());
        for (GHCMessage ghcMessage : ghcMessages) {
            if (new File(ghcMessage.getFileName()).equals(mainFile)) {
                LineColRange lcRange = ghcMessage.getRange();
                TextRange range = lcRange.getRange(psiFile);
                String message = ghcMessage.getErrorMessage();
                CompilerMessageCategory category = ghcMessage.getCategory();
                switch (category) {
                case ERROR:
                    annotationHolder.createErrorAnnotation(range, message);
                    break;
                case WARNING:
                    annotationHolder.createWarningAnnotation(range, message);
                    break;
                case INFORMATION:
                    annotationHolder.createInfoAnnotation(range, message);
                    break;
                case STATISTICS:
                    break;
                }
            }
        }
    }
}
