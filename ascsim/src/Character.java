import java.util.HashMap;

public class Character {


    private Modify strength;
    private Modify agility;
    private Modify intellect;
    private Modify spirit;
    private Modify haste;
    private Modify crit;

    private Modify ap;
    private Modify meleeCritP;
    private Modify meleeHit;
    private Modify expertise;
    private Modify meleeHasteP;

    private Modify rap;
    private Modify rangedCritP;
    private Modify rangedHit;
    private Modify rangedHasteP;

    private Modify sp;
    private Modify holySP;
    private Modify frostSP;
    private Modify holyBonus;
    private Modify frostBonus;
    private Modify holyCritP;
    private Modify frostCritP;
    private Modify spellHit;
    private Modify spellHasteP;


    private HashMap<Integer, Aura> buffs;
    private HashMap<Integer, Aura> debuffs;

    public double getCritP(int spellType, int school) {
        int merged = school*10 + spellType;
        switch (merged) {
            case 12: // holy spell
                return holyCritP.getDouble();
            case 22: // frost spell
                return frostCritP.getDouble();
            case 00: // phys melee
                return meleeCritP.getDouble();
            case 01: // phys ranged
                return rangedCritP.getDouble();
            case 10: // holy melee
                return meleeCritP.getDouble();
            default:
                System.out.println("[Character] no instructions for critP of merged type " + merged);
                return 0.0;
        }
    }

    public int getSpellBonus(int spellType, int school) {
        int merged = school*10 + spellType;
        switch (merged) {
            case 12: // holy spell
                return holyBonus.getInt();
            case 22: // frost spell
                return frostBonus.getInt();
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
        return true;
    }

    public Character(/*read from something*/) {
        this.strength = new Modify(100);
        this.agility = new Modify(100);
        this.intellect = new Modify(100);
        this.spirit = new Modify(100);
        this.haste = new Modify(0);
        this.crit = new Modify(0);

        this.ap = new Modify(0);
        Modify apFromStr = new Modify(100);
        apFromStr.mult(1); // 1 = 2x
        //apFromStr.calculate();
        ap.add(apFromStr);


        this.meleeCritP = new Modify(0);
        this.meleeHit = new Modify(0);
        this.expertise = new Modify(0);
        this.meleeHasteP = new Modify(0);

        this.rap = new Modify(0);
        Modify rapFromAgi = new Modify(agility);
        //rapFromAgi.mult(1);
        rap.add(rapFromAgi);

        this.rangedCritP = new Modify(0);
        this.rangedHit = new Modify(0);
        this.rangedHasteP = new Modify(0);

        this.sp = new Modify(100);
        this.holySP = new Modify(0);
        Modify holySPFromSP = new Modify(sp);
        //holySPFromSP.mult(1);
        holySP.add(holySPFromSP);

        this.frostSP = new Modify(0);
        Modify frostSPFromSP = new Modify(sp);
        //frostSPFromSP.mult(1);
        frostSP.add(frostSPFromSP);

        this.holyBonus = new Modify(0);
        Modify holyBonusFromHolySP = new Modify(holySP);
        holyBonusFromHolySP.mult(1); // 1 for 2x
        holyBonus.add(holyBonusFromHolySP);

        //
        this.frostBonus = new Modify(0);
        this.holyCritP = new Modify(0.5);
        this.frostCritP = new Modify(0);
        this.spellHit = new Modify(0);
        this.spellHasteP = new Modify(0);

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
    public Modify getExpertise() { return this.expertise; }
    public Modify getMeleeHasteP() { return this.meleeHasteP; }

    public Modify getRap() { return this.rap; }
    public Modify getRangedCritP() { return this.rangedCritP; }
    public Modify getRangedHit() { return this.rangedHit; }
    public Modify getRangedHasteP() { return this.rangedHasteP; }

    public Modify getSp() { return this.sp; }
    public Modify getHolySP() { return this.holySP; }
    public Modify getFrostSP() { return this.frostSP; }
    public Modify getHolyBonus() { return this.holyBonus; }
    public Modify getFrostBonus() { return this.frostBonus; }
    public Modify getHolyCritP() { return this.holyCritP; }
    public Modify getFrostCritP() { return this.frostCritP; }
    public Modify getSpellHit() { return this.spellHit; }
    public Modify getSpellHasteP() { return this.spellHasteP; }

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
                "    Melee Crit Chance: " + getMeleeCritP().getDouble()*100.0 + "%\n" +
                "    Melee Hit: " + getMeleeHit().getInt() + "\n" +
                "    Expertise: " + getExpertise().getInt() + "\n" +
                "    Melee Haste: " + getMeleeHasteP().getDouble()*100.0 + "%\n" +
                "\n" +
                "    Ranged Attack Power: " + getRap().getInt() + "\n" +
                "    Ranged Crit Chance: " + getRangedCritP().getDouble()*100.0 + "%\n" +
                "    Ranged Hit: " + getRangedHit().getInt() + "\n" +
                "    Ranged Haste: " + getRangedHasteP().getDouble()*100.0 + "%\n" +
                "\n" +
                "    Spell Power: " + getSp().getInt() + "\n" +
                "      Holy: " + getHolySP().getInt() + "\n" +
                "      Frost: " + getFrostSP().getInt() + "\n" +
                "    Spell Bonus:\n" +
                "      Holy: " + getHolyBonus().getInt() + "\n" +
                "      Frost: " + getFrostBonus().getInt() + "\n" +
                "    Spell Crit:\n" +
                "      Holy: " + getHolyCritP().getDouble()*100.0 + "%\n" +
                "      Frost: " + getFrostCritP().getDouble()*100.0 + "%\n" +
                "    Spell Hit: " + getSpellHit().getInt() + "\n" +
                "    Spell Haste: " + getSpellHasteP().getInt() + "\n";

        return toReturn;
    }
}
