package java.lang;

public final class Boolean implements Comparable<Boolean> {
    // We don't want to allow Boolean instances to be instantiated
    // this way
    private Boolean() {}

    public native static Boolean valueOf(boolean i);

    @Override
    public native int compareTo(Boolean o);
}
