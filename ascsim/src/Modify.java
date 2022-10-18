import java.util.ArrayList;

public class Modify extends SimpleModify {

    private SimpleModify base;
    private double resulting;

    private ArrayList<Modify> mults;
    private ArrayList<SimpleModify> adds;

    private boolean changed;

    public Modify(SimpleModify base) {
        super(base.getDouble());
        this.base = base;
        this.mults = null;
        this.adds = null;
        this.resulting = base.getDouble();

        this.changed = false;
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

    public void mult (double val) {
        mult(val, 0);

    }

    public void mult (int val) {
        mult((double) val, 0);

    }


    public void add(SimpleModify val) {
        changed = true;

        if (adds == null) {
            this.adds = new ArrayList<SimpleModify>();
        }

        resulting += val.getDouble();
        //adds += val;
        adds.add(val);
    }

    public void add(double val) {
        if (adds == null) {
            this.adds = new ArrayList<SimpleModify>();
        }

        resulting += val;
        //adds += val;
        adds.add(new SimpleModify(val));
    }

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

        return (int) resulting;
    }

    public double getDouble() {
        if (changed || base.changed() || anyModChanged()) {
            calculate();
            changed = false;
        }

        return resulting;
    }

    public boolean changed() {
        return changed;
    }
}
