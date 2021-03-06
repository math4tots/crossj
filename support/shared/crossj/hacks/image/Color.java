package crossj.hacks.image;

import crossj.base.AlmostEq;
import crossj.base.Eq;
import crossj.base.List;
import crossj.base.TypedEq;
import crossj.base.XIterable;

/**
 * Color data
 *
 * the to/from I32* methods assume "big endian" (MSB to LSB) when interpreting
 * the colors.
 *
 * So e.g. RGBA means that the red channel occupies the most significant byte.
 * For better or for worse, it just seems that's how these colors tend to be
 * described in most docs I've been reading.
 */
public final class Color implements AlmostEq<Color>, TypedEq<Color> {

    public static final Color WHITE = rgb(1, 1, 1);
    public static final Color BLACK = rgb(0, 0, 0);
    public static final Color RED = rgb(1, 0, 0);
    public static final Color GREEN = rgb(0, 1, 0);
    public static final Color BLUE = rgb(0, 0, 1);
    public static final Color YELLOW = rgb(1, 1, 0);
    public static final Color MAGENTA = rgb(1, 0, 1);
    public static final Color CYAN = rgb(0, 1, 1);

    public final double r;
    public final double g;
    public final double b;
    public final double a;

    private Color(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static Color of(double r, double g, double b, double a) {
        return new Color(r, g, b, a);
    }

    public static Color rgb(double r, double g, double b) {
        return of(r, g, b, 1.0);
    }

    public static Color fromI32RGBA(int rgba) {
        List<Double> channels = splitIntoDouble(rgba);
        return of(channels.get(0), channels.get(1), channels.get(2), channels.get(3));
    }

    /**
     * Creates a color by average all channels of the given colors
     */
    public static Color join(XIterable<Color> colors) {
        double r = 0;
        double g = 0;
        double b = 0;
        double a = 0;
        int total = 0;
        for (var color : colors) {
            total++;
            r += color.r;
            g += color.g;
            b += color.b;
            a += color.a;
        }
        return of(r / total, g / total, b / total, a / total);
    }

    public Color add(Color other) {
        return of(r + other.r, g + other.g, b + other.b, (a + other.a) / 2);
    }

    public Color subtract(Color other) {
        return of(r - other.r, g - other.g, b - other.b, (a + other.a) / 2);
    }

    public Color scale(double factor) {
        return of(r * factor, g * factor, b * factor, a);
    }

    public Color multiply(Color other) {
        return of(r * other.r, g * other.g, b * other.b, a * other.a);
    }

    public Color hadamardProduct(Color other) {
        return of(r * other.r, g * other.g, b * other.b, a * other.a);
    }

    /**
     * Treat (r, g, b) as a vector in R^3, and get the 2-norm squared
     */
    public double rgb2Norm2() {
        return r * r + g * g + b * b;
    }

    public int toI32RGBA() {
        return combineFloats(r, g, b, a);
    }

    public int toI32ARGB() {
        return combineFloats(a, r, g, b);
    }

    public List<Integer> toIntegerList() {
        return split(toI32RGBA());
    }

    private static int floatToIntChannel(double x) {
        // TODO: here and intToFloatChannel: better conversion method
        int i = (int) (x * 256);
        return i < 0 ? 0 : i > 255 ? 255 : i;
    }

    private static double intToFloatChannel(int x) {
        return x == 255 ? 1.0 : ((double) x) / 256;
    }

    private static int combine(int a, int b, int c, int d) {
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static int combineFloats(double a, double b, double c, double d) {
        return combine(floatToIntChannel(a), floatToIntChannel(b), floatToIntChannel(c), floatToIntChannel(d));
    }

    private static List<Integer> split(int x) {
        return List.of((x >>> 24) & 0xFF, (x >>> 16) & 0xFF, (x >>> 8) & 0xFF, x & 0xFF);
    }

    private static List<Double> splitIntoDouble(int x) {
        return split(x).map(i -> intToFloatChannel(i));
    }

    public double getRed() {
        return r;
    }

    public double getGreen() {
        return g;
    }

    public double getBlue() {
        return b;
    }

    public double getAlpha() {
        return a;
    }

    @Override
    public String toString() {
        return "Color.of(" + r + ", " + g + ", " + b + ", " + a + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return rawEquals(obj);
    }

    @Override
    public boolean isEqualTo(Color other) {
        return r == other.r && g == other.g && b == other.b && a == other.a;
    }

    @Override
    public boolean almostEquals(Color other) {
        return Eq.almostForDouble(r, other.r) && Eq.almostForDouble(g, other.g) && Eq.almostForDouble(b, other.b)
                && Eq.almostForDouble(a, other.a);
    }
}
