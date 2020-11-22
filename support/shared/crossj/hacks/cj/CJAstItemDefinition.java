package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Pair;
import crossj.base.Str;
import crossj.base.StrBuilder;

/**
 * Class or trait definition.
 */
public final class CJAstItemDefinition implements CJAstNode {
    private final CJMark mark;
    private final String packageName;
    private final List<CJAstImport> imports;
    private final int modifiers;
    private final String shortName;
    private final List<CJAstTypeParameter> typeParameters;
    private final List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> conditionalTraits;
    private final List<CJAstItemMemberDefinition> members;
    private final Map<String, String> shortToQualifiedNameMap = Map.of();
    private final CJAstTypeParameter selfTypeParameter;

    public CJAstItemDefinition(CJMark mark, String packageName, List<CJAstImport> imports, int modifiers, String shortName,
            List<CJAstTypeParameter> typeParameters,
            List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> conditionalTraits,
            List<CJAstItemMemberDefinition> members) {
        this.mark = mark;
        this.packageName = packageName;
        this.imports = imports;
        this.modifiers = modifiers;
        this.shortName = shortName;
        this.typeParameters = typeParameters;
        this.conditionalTraits = conditionalTraits;
        this.members = members;

        shortToQualifiedNameMap.put(shortName, getQualifiedName());
        for (var imp : imports) {
            var key = splitQualifiedName(imp.getQualifiedName()).get2();
            shortToQualifiedNameMap.put(key, imp.getQualifiedName());
        }

        if (isTrait()) {
            var traitExpression = new CJAstTraitExpression(getMark(), getShortName(),
                getTypeParameters().map(p -> new CJAstTypeExpression(p.getMark(), p.getName(), List.of()))
            );
            selfTypeParameter = new CJAstTypeParameter(getMark(), "Self", List.of(traitExpression));
        } else {
            selfTypeParameter = null;
        }
    }

    @Override
    public CJMark getMark() {
        return mark;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<CJAstImport> getImports() {
        return imports;
    }

    public boolean isTrait() {
        return (modifiers & CJAstItemModifiers.TRAIT) != 0;
    }

    public boolean isClass() {
        return !isTrait();
    }

    public boolean isNative() {
        return (modifiers & CJAstItemModifiers.NATIVE) != 0;
    }

    public String getShortName() {
        return shortName;
    }

    public String getQualifiedName() {
        return packageName + "." + shortName;
    }

    public List<CJAstTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<CJAstTraitExpression> getTraits() {
        return conditionalTraits.map(pair -> pair.get1());
    }

    public List<Pair<CJAstTraitExpression, List<CJAstTypeCondition>>> getConditionalTraits() {
        return conditionalTraits;
    }

    public List<CJAstItemMemberDefinition> getMembers() {
        return members;
    }

    /**
     * Gets the implicit Self type parameter AST node for this item.
     * This method will throw an exception if this item is not a trait.
     *
     * ========== some background ============
     *
     * 'Self' is a way to refer to the self type from inside a trait.
     *
     * 'Self' can be used from inside classes too, but it's simpler there.
     * In classes, 'Self' is more or less an alias for the class with its type parameters
     * applied with the parameters themselves.
     * E.g., in `List[T]`, Self is equivalent to 'List[T]'.
     *
     * However, in traits, it doesn't work that way.
     * For one thing, traits themselves cannot be used as types. So in `Iterable[T]`,
     * 'Iterable[T]' cannot be used as a type (it's a trait). 'Self' should refer
     * instead to the actual class type that ultimately implements 'Iterable[T]'.
     *
     * To accomodate this, there's a 'Self' type parameter that is automatically generated
     * for each trait. Further, all classes must also provide a mechanism for providing
     * each trait it implements with the 'Self' meta object.
     *
     * This way, the 'Self' type should appear like a type parameter that implements
     * the current trait.
     */
    public CJAstTypeParameter getSelfTypeParameter() {
        Assert.that(isTrait());
        return selfTypeParameter;
    }

    /**
     * Materialize a trait object using its own type variables as its type arguments.
     * This is useful in cases when a trait needs to refer to itself inside itself
     * (i.e. for defining the "Self" alias).
     */
    public CJIRTrait getAsSelfTrait() {
        Assert.that(isTrait());
        return new CJIRTrait(this, typeParameters.map(p -> new CJIRVariableType(p, true)));
    }

    /**
     * Materialize a class type object using its own type variables as its type arguments.
     * This is useful in cases when a class needs to refer to itself inside itself
     * (i.e. for defining the "Self" alias).
     */
    public CJIRClassType getAsSelfClassType() {
        Assert.that(!isTrait());
        return new CJIRClassType(this, typeParameters.map(p -> new CJIRVariableType(p, true)));
    }

    @Override
    public void addInspect0(StrBuilder sb, int depth, boolean indentFirstLine, String suffix) {
        if (indentFirstLine) {
            sb.repeatStr("  ", depth);
        }
        sb.s("package ").s(packageName).s("\n");
        for (var imp : imports) {
            imp.addInspect(sb, depth);
        }

        sb.repeatStr("  ", depth).s(isTrait() ? "trait " : "class ").s(shortName).s(" {\n");
        for (var member : members) {
            member.addInspect(sb, depth + 1);
        }
        sb.repeatStr("  ", depth).s("}").s(suffix).s("\n");
    }

    /**
     * Given the context of this item definition, qualify a short item name.
     */
    public String qualifyName(String shortName) {
        return shortToQualifiedNameMap.get(shortName);
    }

    private static Pair<String, String> splitQualifiedName(String qualifiedName) {
        var parts = Str.split(qualifiedName, ".");
        return Pair.of(Str.join(".", parts.slice(0, parts.size() - 1)), parts.get(parts.size() - 1));
    }
}
