/** Spell.java
 *
 * Stores information about a spell, updated to reflect the current game state
 *
 */

import java.sql.ResultSet;
import java.util.HashMap;

public class Spell {

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
        enemyhpmax(0), enemyhpmin(1), hasbuff(2);

        private int val;

        Req(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private int ID;
    private Modify base_dmg_min;
    private Modify base_dmg_max;
    private Modify base_scaling_SP;
    private Modify base_scaling_AP;
    private Modify cast_time;
    private boolean cast_haste;
    private Modify gcd;
    private boolean gcd_haste;
    private Modify cooldown;

    private SpellType spellType;
    private Utils.School school;
    private Utils.School damageSchool;

    private int lastHash;
    private double lastCast;
    private double cdOver;

    private HashMap<Req, Integer> reqs;


    public Spell(int ID, ResultSet record, DBReader reader, GameState gameState) {
        this.ID = ID;

        try {
            // read basic spell fields
            this.base_dmg_min = new Modify(record.getInt("base_dmg_min"));
            this.base_dmg_max = new Modify(record.getInt("base_dmg_max"));
            this.base_scaling_SP = new Modify(record.getDouble("base_scaling_SP") / 100.0); // convert percentage value to decimal value
            this.base_scaling_AP = new Modify(record.getDouble("base_scaling_AP") / 100.0);
            this.cast_time = new Modify(record.getDouble("cast_time"));
            this.cast_haste = record.getBoolean("cast_haste");
            this.gcd = new Modify(record.getDouble("gcd"));
            this.gcd_haste = record.getBoolean("gcd_haste");
            this.cooldown = new Modify(record.getDouble("cooldown"));

            this.spellType = SpellType.valueOf(record.getString("spell_type"));
            this.school = Utils.School.valueOf(record.getString("school"));
            this.damageSchool = Utils.School.valueOf(record.getString("dmg_school"));

            // read spell requirements
            this.reqs = new HashMap<Req, Integer>();

            ResultSet reqSet = reader.doQuery("SELECT conditions FROM spells.spellreq WHERE spell_id=" +
                    String.valueOf(ID) + ";");


            // basic parsing of spell HP or buff requirements
            if(reqSet.next()) {
                String reqStr = reqSet.getString("conditions");
                //System.out.println(reqStr);
                String[] reqSplit = reqStr.split("\\|\\|");
                //System.out.println(reqSplit[0]);
                for (String r : reqSplit) {
                    if (r.contains("enemyhp")) {
                        char comp = r.charAt(7);
                        int val = Integer.parseInt(r.substring(8));

                        switch (comp) {
                            case '<':
                                reqs.put(Req.enemyhpmax, val);
                                break;
                            case '>':
                                reqs.put(Req.enemyhpmin, val);
                                break;
                        }

                        continue;
                    }

                    if (r.contains("hasbuff")) {
                        int buffid = Integer.parseInt(r.substring(8).replace(")", ""));
                        reqs.put(Req.hasbuff, buffid);
                        continue;
                    }
                }
            }

            // query whether this spell has baseline effects
            ResultSet effectSet = reader.doQuery("SELECT * FROM effects.spelleffect WHERE spell_id=" +
                    String.valueOf(ID) + ";");

            if (effectSet.next()) {
                String effectType = effectSet.getString("effect_name");
                String args = effectSet.getString("args");

                switch (effectType) {
                    case "apply_aura_to_target":
                        // load the required aura
                        AuraManager.loadAuraByID(reader, Integer.parseInt(args));

                        Effect effectToDo = new EffectApplyAura(false, Integer.parseInt(args));

                        CLETemplate triggerTemplate = new CLETemplate(CombatLogEvent.Prefix.SPELL, CombatLogEvent.Suffix.CAST_SUCCESS);
                        triggerTemplate.watchParamIndex(0, ID);

                        CLETrigger spellCastTrigger = new CLETrigger(triggerTemplate, effectToDo);
                        gameState.getTriggers().register(spellCastTrigger);


                        break;
                    case "apply_aura_to_self":
                        // TODO i dont think there are any spells with baseline apply to self

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
        if (this.base_dmg_max.getDouble() == 0.0) {
            return null;
        }

        boolean crit = gameState.rand() < gameState.getPlayer().getCritP(spellType.asInt(), school.asInt());
        int amt = rollBaseVal(gameState);
        if (crit) { amt = (int) ((double) amt * 1.5); } // TODO talent changes to crit mod

        CombatLogEvent.SuffixParams sp2 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.DAMAGE, amt, school.asInt(), crit ? 1 : 0);

        // returns spell damage suffix
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
        if (reqs.containsKey(Req.enemyhpmax)) {
            enemyhpcheck = gameState.getTarget(1).getHPP() <= reqs.get(Req.enemyhpmax);
        } else if (reqs.containsKey(Req.enemyhpmin)) {
            enemyhpcheck = gameState.getTarget(1).getHPP() >= reqs.get(Req.enemyhpmax);
        }

        boolean hasbuffcheck = true;

        if (reqs.containsKey(Req.hasbuff)) {
            hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.hasbuff));
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
        if (reqs.containsKey(Req.enemyhpmax)) {
            boolean enemyhpcheck = true;

            if (reqs.containsKey(Req.hasbuff)) {
                boolean hasbuffcheck = true;

                enemyhpcheck = gameState.getTarget(1).getHPP() <= reqs.get(Req.enemyhpmax);
                hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.hasbuff));

                if (!enemyhpcheck && hasbuffcheck) { // player uses a buff to fulfill spell cast requirements
                    //gameState.getPlayer().removeBuffByID(reqs.get(Req.hasbuff)); // consume the buff
                    auraToRemove = reqs.get(Req.hasbuff);
                }
            }

        } else if (reqs.containsKey(Req.enemyhpmin)) {
            boolean enemyhpcheck = true;

            if (reqs.containsKey(Req.hasbuff)) {
                boolean hasbuffcheck = true;

                enemyhpcheck = gameState.getTarget(1).getHPP() >= reqs.get(Req.enemyhpmax);
                hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.hasbuff));

                if (!enemyhpcheck && hasbuffcheck) { // player uses a buff to fulfill spell cast requirements
                    //gameState.getPlayer().removeBuffByID(reqs.get(Req.hasbuff)); // consume the buff
                    auraToRemove = reqs.get(Req.hasbuff);
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

        gameState.invokeCast(dt);
        gameState.invokeGCD(gcdTime);

        CombatLogEvent.PrefixParams pp3 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, this.ID, this.school.asInt());
        CombatLogEvent.SuffixParams sp3 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.CAST_START, -1, -1, -1, -1);
        CombatLogEvent cle_cast_start = new CombatLogEvent(gameState.time(), pp3, sp3, 0, 1);
        cle_cast_start.setSource(0);
        cle_cast_start.setDest(gameState.curTargetID());
        gameState.logEvent(cle_cast_start);

        CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, this.ID, this.school.asInt());
        CombatLogEvent.SuffixParams sp1 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.CAST_SUCCESS, -1, -1, -1, auraToRemove);
        CombatLogEvent cle_cast_success = new CombatLogEvent(gameState.time()+dt, pp1, sp1, 0, 1);
        cle_cast_success.setSource(0);
        cle_cast_success.setDest(gameState.curTargetID());
        gameState.logEvent(cle_cast_success);

        CombatLogEvent.SuffixParams sp2 = rollVal(gameState);
        if (sp2 != null) {
            CombatLogEvent.PrefixParams pp2 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, this.ID, this.school.asInt());
            CombatLogEvent cle_spell_damage = new CombatLogEvent(gameState.time()+dt, pp2, sp2, 0, 1);// destid = 1 default
            // check if glancing, resist, ... maybe in rollval

            // TODO check aoe
            cle_spell_damage.setSource(0);
            cle_spell_damage.setDest(gameState.curTargetID());
            gameState.logEvent(cle_spell_damage);
        }

        //return new Event(dt, gcdTime, cle);
    }

}
