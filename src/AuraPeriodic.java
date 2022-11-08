/** AuraPeriodic.java
 *
 * Models an aura that does periodic damage. This stores base damage, scaling, etc. much like the Spell class
 * Actual values after modifiers are carried in the AuraPeriodicInstance class
 *
 */

public class AuraPeriodic extends Aura {

    private int numTicks;
    private int baseDamageTotal;
    // numTicks and baseDamageTotal shouldn't change - just the values calculated from them (in AuraPeriodicInstance)
    private Modify scalingSP;
    private Modify scalingAP;
    // some procs might change scaling dynamically
    // TODO in this case maybe need to make auramanager not static

    public AuraPeriodic(int spellID, boolean harmful, double duration, Utils.School school, int numTicks, int baseTotalDamage, double scalingSP, double scalingAP) { // TODO int aoeTargets?
        super(spellID, harmful, duration, school, Aura.AuraType.periodic);
        this.numTicks = numTicks;
        this.baseDamageTotal = baseTotalDamage;
        this.scalingSP = new Modify(scalingSP);
        this.scalingAP = new Modify(scalingAP);
    }

    public int getNumTicks() {
        return numTicks;
    }

    public int baseDamageTotal() {
        return baseDamageTotal;
    }

    public Modify getScalingSP() {
        return scalingSP;
    }

    public Modify getScalingAP() {
        return scalingAP;
    }


}
