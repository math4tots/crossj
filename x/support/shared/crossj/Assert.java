package crossj;

public final class Assert {
    private Assert() {
    }

    public static void that(boolean cond) {
        if (!cond) {
            throw XError.withMessage("Assertion failed");
        }
    }

    public static <T> void equals(T a, T b) {
        if (!Eq.of(a, b)) {
            throw XError.withMessage("Assertion failed, expected " + Repr.of(a) + " to equal " + Repr.of(b));
        }
    }

    public static <T> void notEquals(T a, T b) {
        if (Eq.of(a, b)) {
            throw XError.withMessage("Assertion failed, expected " + Repr.of(a) + " to NOT equal " + Repr.of(b));
        }
    }

    public static <A extends Comparable<B>, B> void less(A a, B b) {
        if (a.compareTo(b) >= 0) {
            throw XError.withMessage("Assertion failed, expected " + Repr.of(a) + " to be less than " + Repr.of(b));
        }
    }

    public static <A extends Comparable<B>, B> void notLess(A a, B b) {
        if (a.compareTo(b) < 0) {
            throw XError.withMessage("Assertion failed, expected " + Repr.of(a) + " to be NOT less than " + Repr.of(b));
        }
    }

    public static void raise(Func0<Void> f) {
        boolean thrown = false;
        try {
            f.apply();
        } catch (XError e) {
            thrown = true;
        }
        if (!thrown) {
            throw XError.withMessage("Assertion failed, expected exception not raised");
        }
    }
}
