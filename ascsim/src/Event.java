public class Event {

    private double dt;
    private double gcdTime;
    private CombatLogEvent cle;

    public Event(double dt, double gcdTime, CombatLogEvent cle) {
        this.dt = dt;
        this.gcdTime = gcdTime;
        this.cle = cle;
    }

    public CombatLogEvent cle() {
        return cle;
    }

    public double dt() {
        return dt;
    }

    public double gcd() {
        return gcdTime;
    }

}
