/** Target.java
 *
 * Represents one enemy
 *
 */

public class Target extends Entity {

    private int unitID;
    private double hpp;


    public Target(int unitID) {
        super();
        this.hpp = 100.0;
        this.unitID = unitID;

    }

    public void applyAuraByID(int auraID) {

        //Aura parent = AuraManager.getAuraByID(auraID);
        //AuraInstance auraToApply = new AuraInstance();
    }

    public boolean hasAura(int auraID) {
        if (buffs.containsKey(auraID) || debuffs.containsKey(auraID)) {
            return true;
        }

        return false;
    }



    public double getHPP() {
        return hpp;
    }

    public int getUnitID() {
        return unitID;
    }
}
