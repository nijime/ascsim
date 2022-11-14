public class AuraPeriodicInstance extends AuraInstance {

    private AuraPeriodic parentAura;

    private int numTicks;
    private int baseDamageTotal;
    private double critChance;
    private int bonusDmg;

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




        /// snapshot crit
        this.school = getSchool();
        this.spellType = school == Utils.School.physical ? Spell.SpellType.melee.asInt() : Spell.SpellType.spell.asInt();
        this.critChance = gameState.getPlayer().getCritP(spellType, school.asInt());

        /// snapshot bonus damage
        int spBonus = 0;
        int apBonus = gameState.getPlayer().getAPBonus();
        if (school != Utils.School.physical) {
            // dot is magic
            spBonus = gameState.getPlayer().getSpellBonus(spellType, school.asInt());
        } else {
            // dot is bleed
            spBonus = 0;
        }
        this.bonusDmg = (int) ((parentAura.getScalingSP().getDouble() / 100.0) * spBonus) + (int) ((parentAura.getScalingAP().getDouble() / 100.0) * apBonus);


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

        //int spellType = 0;
        //Utils.School school = getSchool();

        // TODO check if this dot can crit
        boolean crit = gameState.rand() < critChance; // getMeleeCritP


        //int bonusDmg = (int) ((parentAura.getScalingSP().getDouble() / 100.0) * spBonus) + (int) ((parentAura.getScalingAP().getDouble() / 100.0) * apBonus);
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
