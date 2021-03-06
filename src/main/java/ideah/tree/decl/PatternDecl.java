package ideah.tree.decl;

import com.google.common.collect.Iterables;
import ideah.tree.GRHSs;
import ideah.tree.IRange;
import ideah.tree.Located;
import ideah.tree.pat.Pat;

import java.util.Arrays;

public final class PatternDecl extends Bind {

    public final Pat pattern;
    public final GRHSs rightHand;

    public PatternDecl(IRange location, Pat pattern, GRHSs rightHand) {
        super(location);
        this.pattern = pattern;
        this.rightHand = rightHand;
    }

    protected Iterable<Located> getChildren() {
        return Iterables.concat(Arrays.asList(pattern), rightHand.getChildren());
    }
}
