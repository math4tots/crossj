package crossj.iter;

import crossj.base.Repr;

public final class ClassWithCustomRepr implements Repr {
    private final int data;

    public ClassWithCustomRepr(int data) {
        this.data = data;
    }

    @Override
    public String repr() {
        return "<ClassWithCustomRepr data=" + data + ">";
    }

    @Override
    public String toString() {
        return "<ClassWithCustomRepr xx custom toString xx >";
    }
}
