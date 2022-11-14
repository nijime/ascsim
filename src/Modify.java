/** Modify.java
 *
 * Number wrapper used to update values by reference
 *
 * Allows separation of additives and multiplicatives in the form of another Modify
 *
 * Attempts to only update internal values if referenced Modify's have changed
 *
 * Multiplication is performed before addition
 */

import java.util.ArrayList;

public class Modify extends SimpleModify {

    private SimpleModify base;
    private double resulting;

    private ArrayList<Modify> mults;
    private ArrayList<SimpleModify> adds;

    private boolean changed;

    private boolean clamped;
    private double minVal;
    private double maxVal;

    public Modify(SimpleModify base) {
        super(base.getDouble());
        this.base = base;
        this.mults = null;
        this.adds = null;
        this.resulting = base.getDouble();

        this.changed = false;

        clamped = false;
    }

    public Modify(double base) {
        super(base);
        this.base = new SimpleModify(base);
        this.mults = null;
        this.adds = null;
        this.resulting = base;

        this.changed = false;
    }

    public Modify(int base) {
        super(base);
        this.base = new SimpleModify(base);
        this.mults = null;
        this.adds = null;
        this.resulting = base;

        this.changed = false;
    }


    /** mult(double val, int multiplicativity
     *
     * Adds a multiplier with the given base value. Multipliers with the same multiplicativity will be added before being factored
     * into this object's calculation. Multipliers with different multiplicativities will be multiplied before being factored into
     * this object's calculation
     *
     * @param val
     * @param multiplicativity
     */
    public void mult(double val, int multiplicativity) {
        changed = true;

        if (mults == null) {
            this.mults = new ArrayList<Modify>();
            mults.add(new Modify(1.0));
        }

        while (mults.size() <= multiplicativity) {
            mults.add(new Modify(1.0));
        }

        SimpleModify curMult = mults.get(multiplicativity);
        curMult.add(val);
        //mults.set(multiplicativity, curMult + val);
    }

    /** mult (double val)
     *
     * Adds a multiplier with multiplicativity 0 (see mult(double, int))
     *
     * @param val
     */
    public void mult (double val) {
        mult(val, 0);

    }

    /** mult (int val)
     *
     * Adds a multiplier with multiplicativity 0 (see mult(double, int))
     *
     * @param val
     */
    public void mult (int val) {
        mult((double) val, 0);

    }


    /** add(SimpleModify val)
     *
     * Adds the SimpleModify or Modify to the set of adds to be calculated into this object's final value
     *
     * @param val
     */
    public void add(SimpleModify val) {
        changed = true;

        if (adds == null) {
            this.adds = new ArrayList<SimpleModify>();
        }

        resulting += val.getDouble();
        //adds += val;
        adds.add(val);
    }

    /** add(double val)
     *
     * Adds the value to the set of adds to be calculated into this object's final value
     *
     * @param val
     */
    public void add(double val) {
        if (adds == null) {
            this.adds = new ArrayList<SimpleModify>();
        }

        resulting += val;
        //adds += val;
        adds.add(new SimpleModify(val));
    }

    /** calculate
     *
     * Calculates this object's final value from its listed multipliers and adders
     *
     * Multiplication is performed before addition
     */
    public void calculate() {
        double calced = base.getDouble();

        if (mults != null) {
            for (SimpleModify m : mults) {
                calced *= m.getDouble();
            }
        }

        if (adds != null) {
            for (SimpleModify a : adds) {
                calced += a.getDouble();
            }
        }

        //calced += adds;

        resulting = calced;
    }

    /** anyModChanged
     *
     * Returns whether any mult or add contributing to this value has changed
     *
     * @return
     */
    private boolean anyModChanged() {
        if (mults != null) {
            for (SimpleModify m : mults) {
                if (m.changed()) {
                    return true;
                }
            }
        }

        if (adds != null) {
            for (SimpleModify a : adds) {
                if (a.changed()) {
                    return true;
                }
            }
        }


        return false;
    }

    public int getInt() {
        if (changed || base.changed() || anyModChanged()) {
            calculate();
            changed = false;
        }

        if (clamped) {
            return (int) Utils.clamp(resulting, minVal, maxVal);
        }

        return (int) resulting;
    }

    public double getDouble() {
        if (changed || base.changed() || anyModChanged()) {
            calculate();
            changed = false;
        }

        if (clamped) {
            return Utils.clamp(resulting, minVal, maxVal);
        }

        return resulting;
    }

    public boolean changed() {
        return changed;
    }

    public void clamp(double minVal, double maxVal) {
        this.clamped = true;
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    public void unclamp() {
        this.clamped = false;
    }
}
