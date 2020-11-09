package java.lang;

public final class StringBuilder {
    // unfortunately constructors can't be labeled native
    // so we just use a stub here to indicate this exists,
    // but this constructor should really be considered native
    public StringBuilder() {}

    public native void append(Object object);

    public native void appendCodePoint(int codePoint);

    @Override
    public native String toString();
}