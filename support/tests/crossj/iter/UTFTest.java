package crossj.iter;

import crossj.base.Assert;
import crossj.base.Bytes;
import crossj.base.IntArray;
import crossj.base.List;
import crossj.base.Str;
import crossj.base.Test;

/**
 * Content here could all conceivably also live in StringTest
 */
public final class UTFTest {

    @Test
    public static void toUTF8WithbasicMultilingualPlane() {
        Assert.equals(Str.toUTF8("日"), Bytes.ofU8s(230, 151, 165));
        Assert.equals(Str.toUTF8("本"), Bytes.ofU8s(230, 156, 172));
        Assert.equals(Str.toUTF8("日本"), Bytes.ofU8s(230, 151, 165, 230, 156, 172));
    }

    @Test
    public static void toUTF8OutsideBMP() {
        Assert.equals(Str.toUTF8("𩸽"), Bytes.ofU8s(240, 169, 184, 189));
    }

    @Test
    public static void fromUTF8() {
        Assert.equals(Str.fromUTF8(Bytes.ofU8s(230, 151, 165)), "日");
        Assert.equals(Str.fromUTF8(Bytes.ofU8s(230, 156, 172)), "本");
        Assert.equals(Str.fromUTF8(Bytes.ofU8s(230, 151, 165, 230, 156, 172)), "日本");
    }

    @Test
    public static void toUTF32() {
        Assert.equals(Str.toUTF32("日"), IntArray.of(26085));
        Assert.equals(Str.toUTF32("本"), IntArray.of(26412));
        Assert.equals(Str.toUTF32("日本"), IntArray.of(26085, 26412));
        Assert.equals(Str.toUTF32("𩸽"), IntArray.of(171581));
        Assert.equals(Str.toUTF32("hello"), IntArray.of(104, 101, 108, 108, 111));
    }

    @Test
    public static void fromUTF32() {
        Assert.equals(Str.fromCodePoints(List.of(26085)), "日");
        Assert.equals(Str.fromCodePoints(List.of(26412)), "本");
        Assert.equals(Str.fromCodePoints(List.of(26085, 26412)), "日本");
        Assert.equals(Str.fromCodePoints(List.of(171581)), "𩸽");
        Assert.equals(Str.fromCodePoints(List.of(104, 101, 108, 108, 111)), "hello");

        Assert.equals(Str.fromUTF32(IntArray.of(26085)), "日");
        Assert.equals(Str.fromUTF32(IntArray.of(26412)), "本");
        Assert.equals(Str.fromUTF32(IntArray.of(26085, 26412)), "日本");
        Assert.equals(Str.fromUTF32(IntArray.of(171581)), "𩸽");
        Assert.equals(Str.fromUTF32(IntArray.of(104, 101, 108, 108, 111)), "hello");
    }
}
