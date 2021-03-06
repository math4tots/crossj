package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.StrBuilder;

public final class CJAstInstanceMethodCallExpression implements CJAstExpression {
    private final CJMark mark;
    private final String name;
    private final List<CJAstExpression> args;
    CJIRType resolvedType;
    List<CJIRType> inferredTypeArguments;
    CJIRType inferredOwnerType;
    int complexityFlags;

    CJAstInstanceMethodCallExpression(CJMark mark, String name, List<CJAstExpression> args) {
        this.mark = mark;
        this.name = name;
        this.args = args;
        Assert.that(args.size() >= 1);
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public List<CJAstExpression> getArguments() {
        return args;
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s(args.get(0).inspect0()).s(".").s(name).s("(");
        if (args.size() > 1) {
            sb.s("\n").s(args.get(1).inspect0());
            args.get(1).addInspect0(sb, depth + 1, true, ",");
            for (int i = 2; i < args.size(); i++) {
                args.get(i).addInspect0(sb, depth + 1, true, ",");
            }
        }
        sb.s(")").s(suffix).s("\n");
    }

    @Override
    public <R, A> R accept(CJAstExpressionVisitor<R, A> visitor, A a) {
        return visitor.visitInstanceMethodCall(this, a);
    }

    @Override
    public CJIRType getResolvedTypeOrNull() {
        return resolvedType;
    }

    public List<CJIRType> getInferredTypeArguments() {
        Assert.that(inferredTypeArguments != null);
        return inferredTypeArguments;
    }

    public CJIRType getInferredOwnerType() {
        Assert.that(inferredOwnerType != null);
        return inferredOwnerType;
    }

    @Override
    public int getComplexityFlagsOrZero() {
        return complexityFlags;
    }
}
