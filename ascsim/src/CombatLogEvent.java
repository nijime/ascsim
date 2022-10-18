public class CombatLogEvent { // placeholder

    private double time;
    private int ID;
    private int amt;
    private boolean crit;

    public CombatLogEvent(double time, int ID, int amt, boolean crit) {
        this.time = time;
        this.ID = ID;
        this.amt = amt;
        this.crit = crit;
    }

    public double getTime() {
        return time;
    }

    public int getAmt() {
        return amt;
    }

    public int getID() {
        return ID;
    }

    public boolean getCrit() {
        return crit;
    }
}
