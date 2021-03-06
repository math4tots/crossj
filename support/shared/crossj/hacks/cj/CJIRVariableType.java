package crossj.hacks.cj;

import crossj.base.Assert;
import crossj.base.IO;
import crossj.base.List;
import crossj.base.Map;
import crossj.base.Optional;
import crossj.base.Try;
import crossj.base.XError;

public final class CJIRVariableType implements CJIRType {
    private final CJAstTypeParameter definition;
    private final boolean itemLevel;

    CJIRVariableType(CJAstTypeParameter definition, boolean itemLevel) {
        this.definition = definition;
        this.itemLevel = itemLevel;
    }

    public CJAstTypeParameter getDefinition() {
        return definition;
    }

    public boolean isItemLevel() {
        return itemLevel;
    }

    public boolean isMethodLevel() {
        return !itemLevel;
    }

    @Override
    public List<String> getTypeParameterNames() {
        return List.of();
    }

    @Override
    public CJIRType substitute(Map<String, CJIRType> map) {
        var newType = map.getOrNull(definition.getName());
        if (newType == null) {
            /**
             * Substitutions should define a mapping for every type variable that appears in
             * an expression.
             *
             * In some cases, you might think that you need a partial substition, but this
             * should be avoided. For example, consider retrieving a generic method from a
             * variable whose type is a generic class. The variable's type is fully known,
             * so the substitutions related to the class itself are fully bound. But the
             * type arguments for the method's type parameters might not be known yet. In
             * this case, the substitution should be held off completely until all values
             * are known. See e.g. `CJIRMethodDescriptor`.
             */
            throw XError.withMessage("No entry found for '" + definition.getName() + "' in substitution map");
        } else {
            return newType;
        }
    }

    @Override
    public Try<CJIRMethodDescriptor> getMethodDescriptor(String methodName) {
        for (var traitExpression : definition.getAllBounds()) {
            var trait = traitExpression.getAsIsTrait();
            var tryMethodDescriptor = trait.getMethodDescriptor(methodName, this);
            if (tryMethodDescriptor.isFail()) {
                return tryMethodDescriptor.castFail();
            }
            var optionMethodDescriptor = tryMethodDescriptor.get();
            if (optionMethodDescriptor.isPresent()) {
                return Try.ok(optionMethodDescriptor.get());
            }
        }
        IO.println("All bounds = " + definition.getAllBounds().map(e -> e.getAsIsTrait()));
        return Try.fail("Method " + methodName + " not found for type variable " + definition.getName());
    }

    @Override
    public boolean implementsTrait(CJIRTrait trait) {
        for (var implTrait : definition.getAllBounds().map(t -> t.getAsIsTrait())) {
            if (implTrait.implementsTrait(trait)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<CJIRTrait> getImplementingTraitByQualifiedName(String qualifiedName) {
        // TODO: getAllBounds() may be too permissive -- filter out based on
        // actually satisfied bounds.
        for (var implTrait : getAllBounds()) {
            var optTrait = implTrait.getImplementingTraitByQualifiedName(qualifiedName);
            if (optTrait.isPresent()) {
                return optTrait;
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the unconditional bounds on this type variable
     * NOTE: this excludes the conditional ones. For all of them, use getAllBounds()
     */
    public List<CJIRTrait> getBounds() {
        return definition.getBounds().map(t -> t.getAsIsTrait());
    }

    List<CJIRTrait> getAllBounds() {
        return definition.getAllBounds().map(t -> t.getAsIsTrait());
    }

    @Override
    public String toString() {
        return definition.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CJIRVariableType)) {
            return false;
        }
        var other = (CJIRVariableType) obj;
        return definition.getName().equals(other.definition.getName());
    }

    @Override
    public boolean isUnion() {
        return false;
    }

    @Override
    public Optional<String> getClassTypeQualifiedName() {
        return Optional.empty();
    }

    @Override
    public boolean isWrapperType() {
        return false;
    }

    @Override
    public boolean isFunctionType(int argc) {
        return false;
    }

    @Override
    public boolean isTupleType(int argc) {
        return false;
    }

    @Override
    public boolean isNullableType() {
        return false;
    }

    @Override
    public boolean isUnitType() {
        return false;
    }

    @Override
    public boolean isNoReturnType() {
        return false;
    }

    @Override
    public boolean isDerivedFrom(CJAstItemDefinition item) {
        Assert.that(item.isClass());
        return false;
    }
}
