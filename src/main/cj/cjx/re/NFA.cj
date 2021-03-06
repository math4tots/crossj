package cjx.re

import cjx.re.NFABuilder
import cjx.re.RegexNode
import cjx.re.NFARun
import cjx.re.Alphabet


##
## NFA for matching semi-ASCII strings.
##
class NFA {

    def fromRegexNodes(nodes: List[RegexNode]): Self {
        return NFABuilder.buildFromRegexNodeList(nodes)
    }

    val _transitionMap: List[Map[Int, Set[Int]]]
    val _acceptState: Int

    def startState(self: Self): Int {
        return self._acceptState + 1
    }

    def acceptState(self: Self): Int {
        return self._acceptState
    }

    def numberOfAlternatives(self: Self): Int {
        return self._acceptState
    }

    ## Given a set of states, returns a superset of states that includes
    ## all states reachable from the original state with only epsilon state
    ## transitions.
    def epsilonClosureOf[C: Iterable[Int]](self: Self, states: C): Set[Int] {
        val epsilon = Alphabet.epsilon
        val todo = states.toMutableList()
        val closure = Set[Int].builder()
        closure.addAll(todo)

        while (todo.toBool()) {
            val state = todo.pop()
            val neighbors = self._transitionMap.get(state).getOrElse(epsilon, () -> Set.empty())
            for neighbor in neighbors {
                if neighbor not in closure {
                    closure.add(neighbor)
                    todo.add(neighbor)
                }
            }
        }

        val ret = closure.build()
        return ret
    }

    ## Given a set of states and an input letter, returns a new set of states after
    ## the given letter is accepted.
    ##
    ## The returned set accounts for an epsilon closure after the transition.
    def transitionOf[C: Iterable[Int]](self: Self, states: C, letter: Int): Set[Int] {
        val och = letter
        val ch = if (och < 0 or och >= Alphabet.extendedCount) Alphabet.catchall else och
        return self.epsilonClosureOf(Set.fromIterable(
            states.iter().flatMap(
                state -> self._transitionMap.get(state).getOrElse(ch, () -> Set.empty()))
        ))
    }

    ## Given a set of states, returns a set of letters (excluding epsilon) that
    ## appears on an edge coming out from at least oneo f those states.
    def lettersFromStates[C: Iterable[Int]](self: Self, states: C): Set[Int] {
        val epsilon = Alphabet.epsilon
        val builder = Set[Int].builder()
        for state in states {
            for transition in self._transitionMap.get(state).keys() {
                if (transition != epsilon) {
                    builder.add(transition)
                }
            }
        }
        return builder.build()
    }

    def inspect(self: Self): String {
        val epsilon = Alphabet.epsilon
        var sb = String.builder()
        sb.add(
            "=== NFA TRANSITIONS === (start = " +
            self.startState() +
            ", accept = " +
            self.acceptState() +
            ")\n")
        val transitionMap = self._transitionMap
        val acceptState = self._acceptState
        val startState = self.startState()
        for state = 0; state < transitionMap.size(); state += 1 {
            sb.add("  " + state)
            if state < acceptState {
                sb.add(" <accept " + state + ">")
            } else if state == acceptState {
                sb.add(" <universal accept>")
            } else if state == startState {
                sb.add(" <START>")
            }
            sb.add("\n")
            val localMap = transitionMap.get(state)

            localMap.getOrNull(epsilon).ifPresent(newStates -> {
                sb.add("    epsilon -> " + List.sorted(newStates) + "\n")
            })
            for key = 0; key < Alphabet.count; key += 1 {
                localMap.getOrNull(key).ifPresent(newStates -> {
                    var end = key + 1
                    while (
                            end < Alphabet.count and
                            localMap.getOrNull(end) == Nullable.Some(newStates)) {
                        end += 1
                    }
                    sb.add("    ").add(key.toChar().repr())
                    if key + 1 < end {
                        # there are consecutive runs of keys that lead to the same states
                        sb.add("-" + (end - 1).toChar().repr())
                        key = end - 1
                    }
                    sb.add(" -> ").add(List.sorted(newStates).toString()).add("\n")
                })
            }
        }
        return sb.build()
    }

    def start(self: Self): NFARun {
        return NFARun.from(self)
    }

    ## Tries to find the longest matching substring that starts from the beginning
    ## of the given string.
    ##
    ## Returns the length of he match if found, or returns -1 if there is no match.
    def match(self: Self, text: String): Int {
        var lastMatch = -1
        var pos = 0
        val run = self.start()
        if run.isMatching() {
            # check for zero-length match
            lastMatch = pos
        }
        while not run.isDead() and pos < text.size() {
            val letter = text.codeAt(pos)
            run.accept(letter)
            pos += 1
            if run.isMatching() {
                lastMatch = pos
            }
        }
        return lastMatch
    }
}
