public class Pair<T, U> {
    private T key;
    private U val;

    public Pair(T key, U val) {
        this.key = key;
        this.val = val;
    }

    public T getKey() {
        return key;
    }

    public U getValue() {
        return val;
    }
}