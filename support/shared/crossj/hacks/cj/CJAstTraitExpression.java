package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;
import crossj.base.XError;

public final class CJAstTraitExpression implements CJAstNode {
    private final CJMark mark;
    private final String name;
    private final List<CJAstTypeExpression> args;
    private CJIRTrait asIsTrait;

    CJAstTraitExpression(CJMark mark, String name, List<CJAstTypeExpression> args) {
        this.mark = mark;
        this.name = name;
        this.args = args;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public List<CJAstTypeExpression> getArguments() {
        return args;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        Assert.equals(depth, 0);
        Assert.equals(suffix, "");
        sb.s(name);
        if (args.size() > 0) {
            sb.s("[");
            boolean first = true;
            for (var arg : args) {
                if (!first) {
                    sb.s(", ");
                }
                first = false;
                sb.s(arg.inspect());
            }
            sb.s("]");
        }
    }

    public boolean hasAsIsTrait() {
        return asIsTrait != null;
    }

    public CJIRTrait getAsIsTrait() {
        if (asIsTrait == null) {
            throw XError.withMessage("asIsTrait accessed before being set");
        }
        return asIsTrait;
    }

    public void setAsIsTrait(CJIRTrait asIsTrait) {
        if (this.asIsTrait != null) {
            throw XError.withMessage("asIsTrait already set");
        }
        this.asIsTrait = asIsTrait;
    }
}
