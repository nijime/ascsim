/** Character.java
 *
 * Represents the player and stores their stats and aura status
 *
 */


import java.util.HashMap;
import java.util.HashSet;

public class Character extends Entity {
    public static class CharBaseStats {
        private int strength;
        private int agility;
        private int intellect;
        private int spirit;
        private int haste;
        private int crit;
        private int hit;
        private int expertise;

        public CharBaseStats(int strength, int agility, int intellect, int spirit, int haste, int crit, int hit, int expertise) {
            this.strength = strength;
            this.agility = agility;
            this.intellect = intellect;
            this.spirit = spirit;
            this.haste = haste;
            this.crit = crit;
            this.hit = hit;
            this.expertise = expertise;
        }
    }

    private static TalentManager talentManager = new TalentManager();

    private HashMap<Integer, AuraInstance> buffs;
    private HashMap<Integer, AuraInstance> debuffs;

    /* Player stats */
    private Modify strength;
    private Modify agility;
    private Modify intellect;
    private Modify spirit;
    private Modify haste;
    private Modify crit;
    private Modify hit;
    private Modify expertise;

    private Modify ap;
    private Modify meleeCritP;
    private Modify meleeHit;
    private Modify expertiseP;
    private Modify meleeHasteP;

    private Modify rap;
    private Modify rangedCritP;
    private Modify rangedHit;
    private Modify rangedHasteP;

    private Modify sp;
    private Modify holySP;
    private Modify frostSP;
    private Modify shadowSP;
    private Modify natureSP;
    private Modify fireSP;
    private Modify arcaneSP;

    private Modify holyBonus;
    private Modify frostBonus;
    private Modify shadowBonus;
    private Modify natureBonus;
    private Modify fireBonus;
    private Modify arcaneBonus;

    private Modify holyCritP;
    private Modify frostCritP;
    private Modify shadowCritP;
    private Modify natureCritP;
    private Modify fireCritP;
    private Modify arcaneCritP;

    private Modify spellHit;
    private Modify spellHasteP;


    // load values from DB?
    private static final int critUnit = 14; // crit rating per 1% crit chance TODO check value
    private static final int critUnitMelee = 14; //  TODO check value
    private static final int critUnitRanged = 14; //  TODO check value
    private static final int agiCritUnit = 40; // agi per 1% crit chance TODO check value
    private static final int intCritUnit = 40;// int per 1% crit chance TODO check value

    private static final int hasteUnit = 14; // haste rating per 1% haste TODO check value
    private static final int hasteUnitMelee = 14; //  TODO check value
    private static final int hasteUnitRanged = 14; //  TODO check value

    private static final int hitUnit = 21; //  TODO check value
    private static final int hitUnitMelee = 21; //  TODO check value
    private static final int hitUnitRanged = 21; //  TODO check value

    private static final int expertiseUnit = 21; //  TODO check value

    /** deriveFrom
     *
     * Shorthand for below for making one stat depend on another according to:
     * derivedstat = basestat * rate
     * ex. critpercent = crit * (1/14)
     *
     *
     * @param derived
     * @param from
     * @param rate
     */
    private void deriveFrom(Modify derived, Modify from, double rate) {
        Modify statFrom = new Modify(from);
        statFrom.mult(rate - 1.0);
        derived.add(statFrom);
    }

    public Character(CharBaseStats base) {
        Utils.MainStat mainstat = Utils.MainStat.intellect;

        int sb = 1; // # of spell bonus to gain from each spell power
        int strToAP = 1; // # of melee ap to gain from each strength
        int agiToAP = 0; // # of ap to gain from each agi
        if(mainstat == Utils.MainStat.strength) {
            strToAP = 2;
        }

        if(mainstat == Utils.MainStat.agility) {
            agiToAP = 1;
        }

        if(mainstat == Utils.MainStat.intellect) {
            sb = 2;
        }



        this.buffs = new HashMap<Integer, AuraInstance>();
        this.debuffs = new HashMap<Integer, AuraInstance>();

        // placeholder values
        this.strength = new Modify(base.strength);
        this.agility = new Modify(base.agility);
        this.intellect = new Modify(base.intellect);
        this.spirit = new Modify(base.spirit);
        this.haste = new Modify(base.haste);
        this.crit = new Modify(base.crit);
        this.hit = new Modify(base.hit);
        this.expertise = new Modify(base.expertise);

        this.ap = new Modify(0);
        Modify apFromStr = new Modify(strength);
        apFromStr.mult(strToAP-1); // 1 = 2x (add a 100% increase)
        Modify apFromAgi = new Modify(agility);
        if (mainstat == Utils.MainStat.agility) {
            ap.add(apFromAgi);
        }
        ap.add(apFromStr);

        this.meleeCritP = new Modify(0);
        deriveFrom(meleeCritP, crit, 0.01 / (double) critUnitMelee);
        deriveFrom(meleeCritP, crit, 0.01 / (double) agiCritUnit);

        this.meleeHit = new Modify(0);
        deriveFrom(meleeHit, hit, 0.01 / (double) hitUnitMelee);

        this.expertiseP = new Modify(0);
        deriveFrom(expertiseP, expertise, 0.01 / (double) expertiseUnit);

        this.meleeHasteP = new Modify(0);
        deriveFrom(meleeHasteP, haste, 0.01 / (double) hasteUnitMelee);

        this.rap = new Modify(0);
        if (mainstat == Utils.MainStat.agility) {
            rap.add(apFromAgi);
        }

        this.rangedCritP = new Modify(0);
        deriveFrom(rangedCritP, crit, 0.01 / (double) critUnitRanged);
        deriveFrom(rangedCritP, crit, 0.01 / (double) agiCritUnit);

        this.rangedHit = new Modify(0);
        deriveFrom(rangedHit, hit, 0.01 / (double) hitUnitRanged);

        this.rangedHasteP = new Modify(0);
        deriveFrom(rangedHasteP, haste, 0.01 / (double) hasteUnitRanged);

        this.sp = new Modify(100);

        /* school SPs */
        this.holySP = new Modify(0);
        Modify holySPFromSP = new Modify(sp);
        holySP.add(holySPFromSP);

        this.frostSP = new Modify(0);
        Modify frostSPFromSP = new Modify(sp);
        frostSP.add(frostSPFromSP);

        this.shadowSP = new Modify(0);
        Modify shadowSPFromSP = new Modify(sp);
        shadowSP.add(shadowSPFromSP);

        this.natureSP = new Modify(0);
        Modify natureSPFromSP = new Modify(sp);
        natureSP.add(natureSPFromSP);

        this.fireSP = new Modify(0);
        Modify fireSPFromSP = new Modify(sp);
        fireSP.add(fireSPFromSP);

        this.arcaneSP = new Modify(0);
        Modify arcaneSPFromSP = new Modify(sp);
        arcaneSP.add(arcaneSPFromSP);

        /* bonus damages */
        this.holyBonus = new Modify(0);
        Modify holyBonusFromHolySP = new Modify(holySP);
        holyBonusFromHolySP.mult(sb-1); // 1 for 2x
        holyBonus.add(holyBonusFromHolySP);

        this.frostBonus = new Modify(0);
        Modify frostBonusFromFrostSP = new Modify(frostSP);
        frostBonusFromFrostSP.mult(sb-1); // 1 for 2x
        frostBonus.add(frostBonusFromFrostSP);

        this.shadowBonus = new Modify(0);
        Modify shadowBonusFromShadowSP = new Modify(shadowSP);
        shadowBonusFromShadowSP.mult(sb-1); // 1 for 2x
        shadowBonus.add(shadowBonusFromShadowSP);

        this.natureBonus = new Modify(0);
        Modify natureBonusFromNatureSP = new Modify(natureSP);
        natureBonusFromNatureSP.mult(sb-1); // 1 for 2x
        natureBonus.add(natureBonusFromNatureSP);

        this.fireBonus = new Modify(0);
        Modify fireBonusFromFireSP = new Modify(fireSP);
        fireBonusFromFireSP.mult(sb-1); // 1 for 2x
        fireBonus.add(fireBonusFromFireSP);

        this.arcaneBonus = new Modify(0);
        Modify arcaneBonusFromArcaneSP = new Modify(arcaneSP);
        arcaneBonusFromArcaneSP.mult(sb-1); // 1 for 2x
        arcaneBonus.add(arcaneBonusFromArcaneSP);


        /* spell crit */

        Modify spellCritPFromCrit = new Modify(crit);
        spellCritPFromCrit.mult(0.01 / (double) critUnit - 1.0); // aka divide by (critUnit * 100)
        Modify spellCritPFromInt = new Modify(intellect);
        spellCritPFromInt.mult(0.01 / (double) intCritUnit - 1.0); // aka divide by (critUnit * 100)

        this.holyCritP = new Modify(0);
        holyCritP.add(spellCritPFromCrit);
        holyCritP.add(spellCritPFromInt);

        this.frostCritP = new Modify(0);
        frostCritP.add(spellCritPFromCrit);
        frostCritP.add(spellCritPFromInt);

        this.shadowCritP = new Modify(0);
        shadowCritP.add(spellCritPFromCrit);
        shadowCritP.add(spellCritPFromInt);

        this.natureCritP = new Modify(0);
        natureCritP.add(spellCritPFromCrit);
        natureCritP.add(spellCritPFromInt);

        this.fireCritP = new Modify(0);
        fireCritP.add(spellCritPFromCrit);
        fireCritP.add(spellCritPFromInt);

        this.arcaneCritP = new Modify(0);
        arcaneCritP.add(spellCritPFromCrit);
        arcaneCritP.add(spellCritPFromInt);

        /* spell hit and haste */
        this.spellHit = new Modify(0);
        deriveFrom(spellHit, hit, 0.01 / (double) hitUnit);

        this.spellHasteP = new Modify(0);
        deriveFrom(spellHasteP, haste, 0.01 / (double) hasteUnit);
    }


    /** importTalents
     *
     * Loads the character's required talent information from the DB
     *
     * @param db
     * @param toLoad
     * @param gameState
     */
    // s is a string of format "spellid:rank,spellid:rank,..."
    public static void importTalents(DBReader db, HashSet<Pair<Integer, Integer>> toLoad, GameState gameState) {
        talentManager.importTalents(db, toLoad, gameState);
    }


    /* Stat getters */

    /** getCritMod
     *
     * Returns the crit damage multiplier for the given spellType (ex. 1.5 for spells, or 2.0 for melee)
     *
     * @param spellType
     * @return
     */
    public double getCritMod(int spellType) {
        double mod = -1.0;

        switch(spellType) {
            // TODO un-hardcode values
            case 0: // melee
                mod = 2.0;
                break;
            case 1: //ranged
                mod = 2.0;
                break;
            case 2: // spell
                mod = 1.5;
                break;
        }

        return mod;
    }

    /** getCritP
     *
     * Returns the character's crit chance for a spell with the given type (melee, spell, ranged) and school (holy, phys..)
     *
     * @param spellType
     * @param school
     * @return
     */
    public double getCritP(int spellType, int school) {
        int merged = Utils.merge(spellType, school);

        switch (merged) {
            case 12: // holy spell
                return holyCritP.getDouble();
            case 22: // frost spell
                return frostCritP.getDouble();
            case 32: // shadow spell
                return shadowCritP.getDouble();
            case 42: // nature spell
                return natureCritP.getDouble();
            case 52: // fire spell
                return fireCritP.getDouble();
            case 62: // arcane spell
                return arcaneCritP.getDouble();

            case 00: // phys melee
                return meleeCritP.getDouble();
            case 01: // phys ranged
                return rangedCritP.getDouble();
            case 10: // holy melee (is holy melee possible?)
                return meleeCritP.getDouble();
            default:
                System.out.println("[Character] no instructions for critP of merged type " + merged);
                System.exit(-1);
                return 0.0;
        }
    }

    public double getCritP(Spell.SpellType spellType, Utils.School school) {
        return getCritP(spellType.asInt(), school.asInt());
    }

    /** getSpellBonus
     *
     * Returns the appropriate spell power bonus for an ability with the given type and school that has spell power scaling
     *
     * @param spellType
     * @param school
     * @return
     */
    public int getSpellBonus(int spellType, int school) {
        int merged = school*10 + spellType;
        switch (merged) {
            case 12: // holy spell
                return holyBonus.getInt();
            case 22: // frost spell
                return frostBonus.getInt();
            case 32: // shadow spell
                return shadowBonus.getInt();
            case 42: // nature spell
                return natureBonus.getInt();
            case 52: // fire spell
                return fireBonus.getInt();
            case 62: // arcane spell
                return arcaneBonus.getInt();

            case 10: // holy melee
                return holyBonus.getInt();
            default:
                System.out.println("[Character] no instructions for spell bonus of merged type " + merged);
                return 0;
        }
    }

    public int getAPBonus () {
        return ap.getInt();
    }

    public int getRAPBonus () {
        return rap.getInt();
    }

    public boolean hasBuff(int ID) {
        return hasAura(ID);
    }


    public Modify getStrength() { return this.strength; }
    public Modify getAgility() { return this.agility; }
    public Modify getIntellect() { return this.intellect; }
    public Modify getSpirit() { return this.spirit; }
    public Modify getHaste() { return this.haste; }
    public Modify getCrit() { return this.crit; }

    public Modify getAp() { return this.ap; }
    public Modify getMeleeCritP() { return this.meleeCritP; }
    public Modify getMeleeHit() { return this.meleeHit; }
    public Modify getExpertiseP() { return this.expertiseP; }
    public Modify getMeleeHasteP() { return this.meleeHasteP; }

    public Modify getRap() { return this.rap; }
    public Modify getRangedCritP() { return this.rangedCritP; }
    public Modify getRangedHit() { return this.rangedHit; }
    public Modify getRangedHasteP() { return this.rangedHasteP; }

    /*


            physical(0), holy(1), frost(2), shadow(3), nature(4), fire(5), arcane(6),
        // extended schools
        froststorm(7), // nature and frost
        divine(8), // arcane and holy
        elemental(9), // nature, fire, frost
        astral(10), // arcane and nature
        spellfire(11), // arcane and fire
        twilight(12), // shadow and holy
        plague(13), // shadow and nature
        radiant(14); // fire and holy
     */
    public Modify getSp() { return this.sp; }
    public Modify getHolySP() { return this.holySP; }
    public Modify getFrostSP() { return this.frostSP; }
    public Modify getShadowSP() { return this.shadowSP; }
    public Modify getNatureSP() { return this.natureSP; }
    public Modify getFireSP() { return this.fireSP; }
    public Modify getArcaneSP() { return this.arcaneSP; }

    public Modify getHolyBonus() { return this.holyBonus; }
    public Modify getFrostBonus() { return this.frostBonus; }
    public Modify getShadowBonus() { return this.shadowBonus; }
    public Modify getNatureBonus() { return this.natureBonus; }
    public Modify getFireBonus() { return this.fireBonus; }
    public Modify getArcaneBonus() { return this.arcaneBonus; }

    public Modify getHolyCritP() { return this.holyCritP; }
    public Modify getFrostCritP() { return this.frostCritP; }
    public Modify getShadowCritP() { return this.shadowCritP; }
    public Modify getNatureCritP() { return this.natureCritP; }
    public Modify getFireCritP() { return this.fireCritP; }
    public Modify getArcaneCritP() { return this.arcaneCritP; }

    public Modify getSpellHit() { return this.spellHit; }
    public Modify getSpellHasteP() { return this.spellHasteP; }

    // hash for character stats, for checking if Spell needs to be updated
    public int getSpHash() {
        return hashCode();
    }

    public String toString() {
        String toReturn = "Character report:\n" +
                "    Strength: " + getStrength().getInt() + "\n" +
                "    Agility: " + getAgility().getInt() + "\n" +
                "    Intellect: " + getIntellect().getInt() + "\n" +
                "    Spirit: " + getSpirit().getInt() + "\n" +
                "    Haste Rating: " + getHaste().getInt() + "\n" +
                "    Crit Rating: " + getCrit().getInt() + "\n" +
                "\n" +
                "    Attack Power: " + getAp().getInt() + "\n" +
                "    Melee Crit Chance: " + Math.round(getMeleeCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "    Melee Hit: " + Math.round(getMeleeHit().getDouble()*10000.0)/100.0 + "%\n" +
                "    Expertise: " + Math.round(getExpertiseP().getDouble()*10000.0)/100.0 + "%\n" +
                "    Melee Haste: " + Math.round(getMeleeHasteP().getDouble()*10000.0)/100.0 + "%\n" +
                "\n" +
                "    Ranged Attack Power: " + getRap().getInt() + "\n" +
                "    Ranged Crit Chance: " + Math.round(getRangedCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "    Ranged Hit: " + Math.round(getRangedHit().getDouble()*10000.0)/100.0 + "%\n" +
                "    Ranged Haste: " + Math.round(getRangedHasteP().getDouble()*10000.0)/100.0 + "%\n" +
                "\n" +
                "    Spell Power: " + getSp().getInt() + "\n" +
                //"      Holy: " + getHolySP().getInt() + "\n" +
                //"      Frost: " + getFrostSP().getInt() + "\n" +
                "    Spell Bonus:\n" +
                "      Holy: " + getHolyBonus().getInt() + "\n" +
                "      Frost: " + getFrostBonus().getInt() + "\n" +
                "      Shadow: " + getShadowBonus().getInt() + "\n" +
                "      Nature: " + getNatureBonus().getInt() + "\n" +
                "      Fire: " + getFireBonus().getInt() + "\n" +
                "      Arcane: " + getArcaneBonus().getInt() + "\n" +
                "    Spell Crit:\n" +
                "      Holy: " + Math.round(getHolyCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "      Frost: " + Math.round(getFrostCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "      Shadow: " + Math.round(getShadowCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "      Nature: " + Math.round(getNatureCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "      Fire: " + Math.round(getFireCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "      Arcane: " + Math.round(getArcaneCritP().getDouble()*10000.0)/100.0 + "%\n" +
                "    Spell Hit: " + Math.round(getSpellHit().getDouble()*10000.0)/100.0 + "%\n" +
                "    Spell Haste: " + Math.round(getSpellHasteP().getDouble()*10000.0)/100.0 + "%\n";

        return toReturn;
    }
}
