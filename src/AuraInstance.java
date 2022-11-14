/** AuraInstance.java
 *
 * Represents an aura instance which may be applied to an entity
 *
 *
 */

public class AuraInstance {

    private double expirationTime;
    private double applicationTime;
    private Aura parentAura; // aura base which this is an instance of

    public AuraInstance(Aura parentAura, double applicationTime, double expirationTime) {
        this.parentAura = parentAura;
        this.applicationTime = applicationTime;
        this.expirationTime = expirationTime;
    }

    public boolean isHarmful() {
        return parentAura.isHarmful();
    }

    public int getID() {
        return parentAura.getID();
    }

    public double getExpirationTime() {
        return expirationTime;
    }

    public double getApplicationTime() {
        return applicationTime;
    }

    public Utils.School getSchool() {
        return parentAura.getSchool();
    }

    public double getTotalDuration() {
        return expirationTime - applicationTime;
    }

    public void addDuration(double dur) {
        this.expirationTime += dur;
    }

    public void refreshDuration(double curTime) {
        this.expirationTime = curTime + parentAura.getDuration();
    }
}
