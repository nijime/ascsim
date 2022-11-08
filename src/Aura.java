/** Aura.java
 *
 * Represents base values for an aura of given ID, which individual instances should copy from
 *
 */
public class Aura {

    public enum AuraType {

        misc(0), periodic(1);

        private int val;

        AuraType(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }


    private int spellID; // ID of the aura
    private boolean harmful; // buff or debuff
    private double duration; // base duration on application
    private Utils.School school;

    private AuraType auraType;

    // add dispel type ?

    public Aura(int spellID, boolean harmful, double duration) {
        this.spellID = spellID;
        this.harmful = harmful;
        this.duration = duration;

        this.school = Utils.School.holy; // TODO default
        this.auraType = AuraType.misc;
    }

    public Aura(int spellID, boolean harmful, double duration, Utils.School school) {
        this.spellID = spellID;
        this.harmful = harmful;
        this.duration = duration;

        this.school = school;
        this.auraType = AuraType.misc;
    }

    public Aura(int spellID, boolean harmful, double duration, Utils.School school, AuraType auraType) {
        this.spellID = spellID;
        this.harmful = harmful;
        this.duration = duration;

        this.school = school;

        this.auraType = auraType;
    }

    public AuraType getAuraType() {
        return auraType;
    }

    public boolean isHarmful() {
        return harmful;
    }

    public double getDuration() {
        return duration;
    }

    public Utils.School getSchool() {
        return school;
    }

    public int getID() {
        return spellID;
    }
}
