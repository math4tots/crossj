package crossj.books.dragon.ch03.nfa;

/**
 * class that allows creating various RegexNodes.
 */
public final class RegexBuilder {
    RegexBuilder() {}

    public RegexNode epsilon() {
        return new EpsilonRegexNode();
    }

    public RegexNode letter(int letter) {
        return new LetterRegexNode(letter);
    }

    public RegexNode cat(RegexNode first, RegexNode... rest) {
        for (var node : rest) {
            first = new CatRegexNode(first, node);
        }
        return first;
    }

    public RegexNode alt(RegexNode first, RegexNode... rest) {
        for (var node : rest) {
            first = new OrRegexNode(first, node);
        }
        return first;
    }

    public RegexNode star(RegexNode inner) {
        return new StarRegexNode(inner);
    }

    static String wrap(RegexNode node, int outerPrecedence) {
        return node.getBindingPrecedence() < outerPrecedence ? "(" + node.toPattern() + ")" : node.toPattern();
    }
}
