package debili;


import java.util.function.Supplier;

public class Thunk<T> {
    private T value;
    private Supplier<T> supplier;

    private Thunk(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (supplier != null) {
            value = supplier.get();
            supplier = null;
        }
        return value;
    }

    public static <T> Thunk<T> delay(Supplier<T> supplier) {
        return new Thunk<>(supplier);
    }
    public static <T> Thunk<T> now(T v){
         return new Thunk<>(()->v);
    }
}
