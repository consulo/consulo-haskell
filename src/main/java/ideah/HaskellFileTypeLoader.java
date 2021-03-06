package ideah;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.util.text.StringUtil;

public final class HaskellFileTypeLoader extends FileTypeFactory {

    public static final List<FileType> HASKELL_FILE_TYPES = new ArrayList<FileType>();

    public void createFileTypes(@Nonnull FileTypeConsumer consumer) {
        consumer.consume(HaskellFileType.INSTANCE, StringUtil.join(new String[] {"hs", "lhs"}, ";"));
        HASKELL_FILE_TYPES.add(HaskellFileType.INSTANCE);
    }
}
