package crossj.hacks.cj;

import crossj.base.List;
import crossj.base.Optional;
import crossj.base.StrBuilder;

public final class CJAstMethodDefinition implements CJAstItemMemberDefinition {
    private final CJMark mark;
    private final List<CJAstTypeCondition> typeConditions;
    private final Optional<String> comment;
    private final int modifiers;
    private final String name;
    private final List<CJAstTypeParameter> typeParameters;
    private final List<CJAstParameter> parameters;
    private final CJAstTypeExpression returnType;
    private final Optional<CJAstBlockStatement> body;

    CJAstMethodDefinition(CJMark mark, List<CJAstTypeCondition> typeConditions, Optional<String> comment, int modifiers,
            String name, List<CJAstTypeParameter> typeParameters, List<CJAstParameter> parameters,
            CJAstTypeExpression returnType, Optional<CJAstBlockStatement> body) {
        this.mark = mark;
        this.typeConditions = typeConditions;
        this.modifiers = modifiers;
        this.comment = comment;
        this.name = name;
        this.typeParameters = typeParameters;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public List<CJAstTypeCondition> getTypeConditions() {
        return typeConditions;
    }

    @Override
    public Optional<String> getComment() {
        return comment;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<CJAstTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstParameter> getParameters() {
        return parameters;
    }

    public CJAstTypeExpression getReturnType() {
        return returnType;
    }

    public Optional<CJAstBlockStatement> getBody() {
        return body;
    }

    @Override
    public <R, A> R accept(CJAstItemMemberVisitor<R, A> visitor, A a) {
        return visitor.visitMethod(this, a);
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("def ").s(name);
        if (typeParameters.size() > 0) {
            sb.s("[");
            boolean first = true;
            for (var tp : typeParameters) {
                if (!first) {
                    sb.s(", ");
                }
                first = false;
                sb.s(tp.inspect());
            }
            sb.s("]");
        }
        sb.s("(");
        boolean first = true;
        for (var p : parameters) {
            if (!first) {
                sb.s(", ");
            }
            first = false;
            sb.s(p.inspect());
        }
        sb.s(") : ").s(returnType.inspect());
        if (body.isEmpty()) {
            sb.s("\n");
        } else {
            sb.s(" ");
            body.get().addInspect0(sb, depth, false, "");
        }
    }
}
