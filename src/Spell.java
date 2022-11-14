/** Spell.java
 *
 * Stores information about a spell, updated to reflect the current game state
 *
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Spell {
    // TODO check value
    private static final double SPELL_HITCAP = 18.0;
    private static final double RANGED_HITCAP = 12.0;
    private static final double MELEE_HITCAP = 7.0;

    public enum SpellType {
        melee(0), ranged(1), spell(2);

        private int val;

        SpellType(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }


    private enum Req {
        min_health(1), max_health(2), has_buff(3), target_has_debuff(4);

        private int val;

        Req(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private int ID;

    private SpellType spellType;
    private Utils.School school;
    private boolean isChannelled;
    private Utils.Resource resourceType;
    private Modify resourceCost;
    private Modify cast_time;
    private Modify gcd;
    private Modify cooldown;
    private boolean cast_haste;
    private boolean gcd_haste;

    private Utils.School damageSchool;
    private int numTargets;
    private boolean splitDmg;
    private Modify base_dmg_min;
    private Modify base_dmg_max;
    private Modify numHits;
    private Modify base_scaling_SP;
    private Modify base_scaling_AP;

    private boolean weaponScaling;
    private double daggerMH;
    private double mediumMH;
    private double largeMH;
    private double daggerOH;
    private double mediumOH;
    private double largeOH;

    private int lastHash;
    private double lastCast;
    private double cdOver;

    private HashMap<Req, Integer> reqs;


    public boolean hasDamage() {
        return damageSchool != null;
    }

    public boolean hasWeaponScaling() {
        return weaponScaling;
    }

    public boolean hasCost() {
        return resourceType != null;
    }

    public Spell(int ID, ResultSet spellTable, DBReader reader, GameState gameState) {
        this.ID = ID;

        try {
            /// fields from Spell table
            this.spellType = SpellType.values()[spellTable.getInt("typeID")];
            this.school = Utils.School.values()[spellTable.getInt("schoolID")];
            this.isChannelled = spellTable.getBoolean("isChannelled");
            this.resourceType = Utils.Resource.values()[spellTable.getInt("resourceType")];
            this.resourceCost = new Modify(spellTable.getInt("cost"));
            this.cast_time = new Modify(spellTable.getDouble("castTime"));
            this.gcd = new Modify(spellTable.getDouble("GCD"));
            this.cooldown = new Modify(spellTable.getDouble("cooldown"));

            switch (spellType) {
                case melee:
                    this.cast_haste = false;
                    this.gcd_haste = false;
                    break;
                case ranged: // TODO check
                    this.cast_haste = false;
                    this.gcd_haste = false;
                    break;
                case spell:
                    this.cast_haste = true;
                    this.gcd_haste = true;
                    break;

            }

            /// fields from Spelldamage table
            this.damageSchool = Utils.School.values()[spellTable.getInt("dmgSchool")];
            if (spellTable.wasNull()) { // no Spelldamage records for this spell
                this.damageSchool = null;
            } else {
                this.numTargets = spellTable.getInt("numTargets");
                this.splitDmg = spellTable.getBoolean("split");
                this.base_dmg_min = new Modify(spellTable.getInt("dmgMin"));
                this.base_dmg_max = new Modify(spellTable.getInt("dmgMax"));
                this.numHits = new Modify(spellTable.getInt("numHits"));
                this.base_scaling_SP = new Modify(spellTable.getDouble("scalingSP") / 100.0); // convert percentage value to decimal value
                this.base_scaling_AP = new Modify(spellTable.getDouble("scalingAP") / 100.0);
            }


            /// fields from Weaponscaling table
            int numNull = 0;
            this.daggerMH = spellTable.getDouble("daggerMH");
            if (spellTable.wasNull()) {
                numNull++;
            }
            this.mediumMH = spellTable.getDouble("mediumMH");
            if (spellTable.wasNull()) {
                numNull++;
            }
            this.largeMH = spellTable.getDouble("largeMH");
            if (spellTable.wasNull()) {
                numNull++;
            }
            this.daggerOH = spellTable.getDouble("daggerOH");
            if (spellTable.wasNull()) {
                numNull++;
            }
            this.mediumOH = spellTable.getDouble("mediumOH");
            if (spellTable.wasNull()) {
                numNull++;
            }
            this.largeOH = spellTable.getDouble("largeOH");
            if (spellTable.wasNull()) {
                numNull++;
            }

            this.weaponScaling = true;
            if (numNull >= 6) {
                this.weaponScaling = false;
            }


            /// read spell requirements
            this.reqs = new HashMap<Req, Integer>();

            ResultSet reqSet = reader.doQuery("SELECT * FROM spells.SpellReq WHERE spellID=" +
                    String.valueOf(ID) + ";");


            /// basic parsing of spell HP or buff requirements
            try {
                while(reqSet.next()) {
                    Req reqType = Req.values()[reqSet.getInt("reqType")-1]; /// subtract 1 because DB indexes at 1
                    int arg1 = reqSet.getInt("reqArg1");

                    reqs.put(reqType, arg1);

                }
            } catch (SQLException e) {
                System.out.println("[Spell] Failed to import spell requirements for " +  ID);
                e.printStackTrace();
            }

            // query whether this spell has baseline effects
            ResultSet effectSet = reader.doQuery("SELECT * FROM spells.SpellEffect WHERE spellID=" +
                    String.valueOf(ID) + ";");

            if (effectSet.next()) {
                Utils.EffectType effectType = Utils.EffectType.values()[effectSet.getInt("effectID")];
                double effectChance = effectSet.getDouble("effectChance");
                int arg1 = effectSet.getInt("intArg1");
                if (effectSet.wasNull()) {
                    arg1 = -2;
                }
                int arg2 = effectSet.getInt("intArg2");
                if (effectSet.wasNull()) {
                    arg2 = -2;
                }
                double arg3 = effectSet.getDouble("doubleArg");
                if (effectSet.wasNull()) {
                    arg3 = -2.0;
                }
                String arg4 = effectSet.getString("strArg");

                switch (effectType) {
                    case apply_aura_to_caster:
                        // TODO
                        break;
                    case apply_aura_to_target:
                        // load the required aura
                        AuraManager.loadAuraByID(reader, arg1);


                        Effect effectToDo = new EffectApplyAura(false, arg1);

                        CLETemplate triggerTemplate = new CLETemplate(CombatLogEvent.Prefix.SPELL, CombatLogEvent.Suffix.CAST_SUCCESS);
                        triggerTemplate.watchParamIndex(0, ID); // watch for cast successes with this ID
                        triggerTemplate.watchParamIndex(2, 0); // watch for cast successes that dont miss

                        CLETrigger spellCastTrigger = new CLETrigger(triggerTemplate, effectToDo);
                        spellCastTrigger.setChance(effectChance);
                        gameState.getTriggers().register(spellCastTrigger);

                        break;
                    default:
                        // do nothing
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Spell] Failed to read fields for spell ID: " + String.valueOf(ID));
        }

        this.lastHash = -1;



    }

    /** rollBaseVal
     *
     * Rolls a random number in between the spell's min and max values, then applies spell bonus / AP bonus
     *
     * @param gameState
     * @return
     */
    private int rollBaseVal(GameState gameState) {
        double random = gameState.rand();
        int rolled = base_dmg_min.getInt() + (int) (random*(base_dmg_max.getDouble() - base_dmg_min.getDouble()));
        int apBonus = (int) (base_scaling_AP.getDouble() * (double) gameState.getPlayer().getAPBonus());
        int rapBonus = (int) (base_scaling_AP.getDouble() * (double) gameState.getPlayer().getRAPBonus());
        int spellBonus = (int) (base_scaling_SP.getDouble() * (double) gameState.getPlayer().getSpellBonus(spellType.asInt(), school.asInt()));
        int afterBonus = rolled + apBonus + rapBonus + spellBonus;
        return afterBonus;
    }

    /** rollVal
     *
     * Rolls whether the spell should crit and returns the _DAMAGE suffix that reflects the determined values
     *
     * @param gameState
     * @return
     */
    private CombatLogEvent.SuffixParams rollVal(GameState gameState) {
        /// roll for miss TODO glancing, parry, resist, ...
        boolean spellMiss = false;
        int missType = 1;
        switch (spellType) {
            case melee:
                spellMiss = gameState.rand() < (MELEE_HITCAP / 100.0 - gameState.getPlayer().getMeleeHit().getDouble());
                missType = 1;
                break;
            case ranged:
                spellMiss = gameState.rand() < (RANGED_HITCAP / 100.0 - gameState.getPlayer().getRangedHit().getDouble());
                missType = 1;
                break;
            case spell:
                spellMiss = gameState.rand() < (SPELL_HITCAP / 100.0 - gameState.getPlayer().getSpellHit().getDouble());
                missType = 1;
                break;
        }

        if (spellMiss) {
            /// return MISSED suffix
            return new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.MISSED, missType, -1, -1);
        }

        /// a spell with no damage can miss, but can't cause a SPELL_DAMAGE event
        if (!hasDamage()) {
            return null;
        }

        boolean crit = gameState.rand() < gameState.getPlayer().getCritP(spellType.asInt(), school.asInt());
        int amt = rollBaseVal(gameState);
        if (crit) { amt = (int) ((double) amt * 1.5); } // TODO talent changes to crit mod

        CombatLogEvent.SuffixParams sp2 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.DAMAGE, amt, school.asInt(), crit ? 1 : 0);

        /// return damage suffix
        return sp2;
    }

    /** update
     *
     * TODO update spell dynamically
     *
     * @param gameState
     */
    private void update(GameState gameState) {

    }

    /**
     *
     * Returns whether this spell can be cast currently. This checks whether the spell is off CD and whether enemy HP/buff
     * requirements are met
     *
     * @param gameState
     * @return
     */
    public boolean canCast(GameState gameState) {
        boolean cdCheck = gameState.time() >= cdOver;//(gameState.time() - lastCast) >= cooldown.getDouble();
        boolean enemyhpcheck = true;

        // hardcoded to check from target 1
        if (reqs.containsKey(Req.max_health)) {
            enemyhpcheck = gameState.getTarget(1).getHPP() <= reqs.get(Req.max_health);
        } else if (reqs.containsKey(Req.min_health)) {
            enemyhpcheck = gameState.getTarget(1).getHPP() >= reqs.get(Req.min_health);
        }

        boolean hasbuffcheck = true;

        if (reqs.containsKey(Req.has_buff)) {
            hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.has_buff));
        }

        return cdCheck && (enemyhpcheck || hasbuffcheck);
    }

    /** cast
     *
     * Casts the spell and sends related CLEs to the combatlog
     *
     * @param gameState
     */
    public void cast(GameState gameState) {
        //// checks for consuming required buff //// this is now a part of CLE postprocess

        // TODO hardcoded to check from target 1

        // determine whether an aura should be removed
        int auraToRemove = -1;
        if (reqs.containsKey(Req.max_health)) {
            boolean enemyhpcheck = true;

            if (reqs.containsKey(Req.has_buff)) {
                boolean hasbuffcheck = true;

                enemyhpcheck = gameState.getTarget(1).getHPP() <= reqs.get(Req.max_health);
                hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.has_buff));

                if (!enemyhpcheck && hasbuffcheck) { // player uses a buff to fulfill spell cast requirements
                    //gameState.getPlayer().removeBuffByID(reqs.get(Req.hasbuff)); // consume the buff
                    auraToRemove = reqs.get(Req.has_buff);
                }
            }

        } else if (reqs.containsKey(Req.min_health)) {
            boolean enemyhpcheck = true;

            if (reqs.containsKey(Req.has_buff)) {
                boolean hasbuffcheck = true;

                enemyhpcheck = gameState.getTarget(1).getHPP() >= reqs.get(Req.max_health);
                hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.has_buff));

                if (!enemyhpcheck && hasbuffcheck) { // player uses a buff to fulfill spell cast requirements
                    //gameState.getPlayer().removeBuffByID(reqs.get(Req.hasbuff)); // consume the buff
                    auraToRemove = reqs.get(Req.has_buff);
                }
            }
        }
        ////

        int h;
        if ((h = gameState.getSpHash()) != lastHash) {
            this.lastHash = h;
            update(gameState);
        }

        this.lastCast = gameState.time();
        this.cdOver = gameState.time() + cooldown.getDouble();


        double dt = cast_time.getDouble();
        double gcdTime = gcd.getDouble();

        if (spellType == SpellType.spell) {
            /// assume all and only spells benefit from haste
            dt = dt / (1.0 + gameState.getPlayer().getSpellHasteP().getDouble());
            gcdTime = gcdTime / (1.0 + gameState.getPlayer().getSpellHasteP().getDouble());
        }

        double damageTime = gameState.time() + dt; /// time when damage or miss should occur

        gameState.invokeCast(dt);
        gameState.invokeGCD(gcdTime);

        /// cast start event
        CombatLogEvent.PrefixParams pp3 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, this.ID, this.school.asInt());
        CombatLogEvent.SuffixParams sp3 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.CAST_START, -1, -1, -1, -1);
        CombatLogEvent cle_cast_start = new CombatLogEvent(gameState.time(), pp3, sp3, 0, 1);
        cle_cast_start.setSource(0);
        cle_cast_start.setDest(gameState.curTargetID());
        gameState.logEvent(cle_cast_start);

        /// cast damage OR cast missed event

        CombatLogEvent cle_spell_damage = null;
        CombatLogEvent.SuffixParams sp2 = rollVal(gameState);
        int missType = 0; /// if missed, pass that info the CAST_SUCCESS
        if (sp2 != null) {
            if (sp2.suffix == CLEDescriptor.Suffix.MISSED) {
                missType = sp2.param1; /// if missed, pass that info the CAST_SUCCESS
            }

            CombatLogEvent.PrefixParams pp2 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, this.ID, this.school.asInt());
            cle_spell_damage = new CombatLogEvent(damageTime, pp2, sp2, 0, 1);// destid = 1 default

            // TODO check aoe
            cle_spell_damage.setSource(0);
            cle_spell_damage.setDest(gameState.curTargetID());

        }

        /// cast success event
        CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, this.ID, this.school.asInt());
        CombatLogEvent.SuffixParams sp1 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.CAST_SUCCESS, missType, -1, -1, auraToRemove);
        CombatLogEvent cle_cast_success = new CombatLogEvent(damageTime, pp1, sp1, 0, 1);
        cle_cast_success.setSource(0);
        cle_cast_success.setDest(gameState.curTargetID());
        gameState.logEvent(cle_cast_success);

        if (cle_spell_damage != null) {
            gameState.logEvent(cle_spell_damage); /// log damage after success, matters for instant cast spells
            /// if damage occurs before success, summary might fail to log a hit or miss
        }

    }

    /** CDOverTime
     *
     * Return the time at which this spell's CD will be over
     *
     * @return
     */
    public double CDOverTime() {
        return cdOver;
    }

}
