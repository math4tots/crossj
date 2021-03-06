package crossj.base;

/**
 * Utility for iterating over and interacting with subslices of a string
 */
public final class StrIter {
    native static StrIter of(String string);

    native public String sliceFrom(int start);

    /**
     * Returns the codePoint at the current position in the string. You should call
     * the 'hasCodePoint()' method to check that we haven't moved past the end of
     * the string.
     */
    native public int peekCodePoint();

    /**
     * Returns the codePoint at the current position, and increments the position.
     * You should call 'hashCodePoint()' to check that we are not yet past the
     * end of the string.
     */
    native public int nextCodePoint();

    /**
     * Increments this iterator to the next codepoint in the string.
     */
    native public void incr();

    /**
     * Decrements this iterator to the previous codepoint in the string.
     */
    native public void decr();

    /**
     * Increment this iterator n times
     */
    native public void incrN(int n);

    /**
     * Decrement this iterator n times
     */
    native public void decrN(int n);

    /**
     * Returns true, there are still more characters to process and getCodePoint()
     * will return a valid value. Otherwise, we have seeked past the end of the
     * given string.
     */
    native public boolean hasCodePoint();

    /**
     * Checks if the substring starting form the current position to the end starts
     * with the given prefix.
     */
    native public boolean startsWith(String prefix);

    /**
     * Rewinds the iterator to the beginning of the string
     */
    native public void seekToStart();

    /**
     * Moves the iterator to the end of the string
     */
    native public void seekToEnd();

    native public int getPosition();

    native public void setPosition(int i);

    /**
     * Checks if the substring starting from the beginning of the string to the
     * current position ends with the given suffix.
     */
    native public boolean endsWith(String suffix);

    native public String getString();
}
