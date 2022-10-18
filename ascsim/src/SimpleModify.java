import java.util.ArrayList;

public class SimpleModify {

    private double base;

    private boolean changed;

    public SimpleModify(double base) {
        this.base = base;
    }

    public SimpleModify(int base) {
        this.base = (double) base;
    }

    public void mult(int val) {
        changed = true;
        this.base = this.base * (double) val;
    }

    public void mult(double val) {
        changed = true;
        this.base = this.base * val;
    }

    public void add(double val) {
        changed = true;
        this.base = this.base + val;
    }

    public void add(int val) {
        changed = true;
        this.base = this.base + (double) val;
    }

    public int getInt() {
        if (changed) {
            changed = false;
        }

        return (int) base;
    }

    public double getDouble() {
        if (changed) {
            changed = false;
        }

        return base;
    }

    public boolean changed() {
        return changed;
    }
}
