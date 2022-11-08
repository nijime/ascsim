public class AuraPeriodicInstance extends AuraInstance {

    private AuraPeriodic parentAura;

    private int numTicks;
    private int baseDamageTotal;
    private double critChance;
    private Modify scalingSP;
    private Modify scalingAP;
    // TODO snapshot spell power and total damage on application
    
    private double tickTime;

    private GameState gameState;

    private int spellType;
    private Utils.School  school;

    public AuraPeriodicInstance(AuraPeriodic parentAura, double applicationTime, double expirationTime, GameState gameState) {
        super(parentAura, applicationTime, expirationTime);
        this.parentAura = parentAura;
        // TODO populate variables

        this.numTicks = parentAura.getNumTicks();
        this.baseDamageTotal = parentAura.baseDamageTotal();
        // snapshot crit
        this.school = getSchool();
        this.spellType = school == Utils.School.physical ? Spell.SpellType.melee.asInt() : Spell.SpellType.spell.asInt();
        this.critChance = gameState.getPlayer().getCritP(spellType, school.asInt());

        // dont need scaling in the instance of aura ?
        tickTime = (expirationTime - applicationTime) / (double) numTicks;

        this.gameState = gameState;

    }

    public double getCritChance() {
        return critChance;
    }

    /** tickDamage
     *
     * Returns the CLE suffix for the damage that one tick should do, after modifiers, and rolls for crit
     *
     * @return
     */
    public CLEDescriptor.SuffixParams tickDamage() {
        int spBonus = 0;
        int apBonus = gameState.getPlayer().getAPBonus();

        //int spellType = 0;
        //Utils.School school = getSchool();

        boolean crit = false;

        // TODO hybrid dots?
        if (school != Utils.School.physical) {
            // dot is magic
            spellType = Spell.SpellType.spell.asInt();
            spBonus = gameState.getPlayer().getSpellBonus(spellType, school.asInt());
            // TODO check if this dot can crit
            crit = gameState.rand() < critChance;

        } else {
            // dot is bleed
            spellType = Spell.SpellType.melee.asInt();
            spBonus = 0;

            // TODO check if this dot can crit
            crit = gameState.rand() < critChance; // getMeleeCritP
        }

        int bonusDmg = (int) ((parentAura.getScalingSP().getDouble() / 100.0) * spBonus) + (int) ((parentAura.getScalingAP().getDouble() / 100.0) * apBonus);
        int dmg = (baseDamageTotal / numTicks) + bonusDmg;

        if (crit) { dmg = (int) ((double) dmg * gameState.getPlayer().getCritMod(spellType)); } // TODO talent changes to crit mod

        CombatLogEvent.SuffixParams sp1 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.DAMAGE, dmg, school.asInt(), crit ? 1 : 0);

        return sp1;
    }

    public int getNumTicks() {
        return numTicks;
    }

    public double getTickTime() {
        return tickTime;
    }

}
