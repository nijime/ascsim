/** Talent.java
 *
 * Reads talent info from the database and saves the effects of each talent
 * ex. talents may add proc chances or add a % modifier to a type of damage
 *
 */

import java.sql.ResultSet;

public class Talent {

    private int spellID;
    private int rank;

    public Talent(int spellID, int rank) {
        this.spellID = spellID;
        this.rank = rank;
    }

    /** loadSpellEffect
     *
     * Queries the DB and stores the information retrieved in spellEffect
     *
     * TODO account for percent modifiers
     *
     * @param reader
     * @param triggerManager
     */
    public void loadSpellEffectTriggers(DBReader reader, TriggerManager triggerManager) {

        ResultSet result = reader.doQuery("SELECT * FROM talents.TalentSpellEffect " +
                " WHERE talentID = '" + spellID + "' " +
                " and talentRank = '" + rank + "' " +
                ";");

        try {
            while (result.next()) {
                int spellSpellID = result.getInt("spellID");
                Utils.EffectType effectType = Utils.EffectType.values()[result.getInt("effectID") - 1]; /// subtract because DB indexes at 1
                double chance = result.getDouble("effectChance");

                int arg1 = result.getInt("intArg1");
                if (result.wasNull()) {
                    arg1 = -2;
                }
                int arg2 = result.getInt("intArg2");
                if (result.wasNull()) {
                    arg2 = -2;
                }
                double arg3 = result.getDouble("doubleArg");
                if (result.wasNull()) {
                    arg3 = -2.0;
                }
                String arg4 = result.getString("strArg");

                Effect thisEffect = null;

                /// create template beforehand
                CLETemplate template = new CLETemplate(CLEDescriptor.Prefix.SPELL, CLEDescriptor.Suffix.CAST_SUCCESS);
                template.watchParamIndex(0, spellSpellID); // watch for matching spell ID

                switch (effectType) {
                    case apply_aura_to_caster:
                        thisEffect = new EffectApplyAura(true, arg1); // TODO currently only allowing independent procs of single effects (separate args out, use array?)
                        AuraManager.loadAuraByID(reader, arg1);
                        break;
                    case apply_aura_to_target:
                        thisEffect = new EffectApplyAura(false, arg1); // TODO currently only allowing independent procs of single effects (separate args out, use array?)
                        AuraManager.loadAuraByID(reader, arg1); /// load necessary auras as we find them
                        template.watchParamIndex(2, 0); /// watch for non-misses
                        /// offensive aura applications should only happen if the spell hits
                        break;
                        //TODO other effectTypes
                }

                CLETrigger trigger = new CLETrigger(template, thisEffect);
                trigger.setChance(chance);

                triggerManager.register(trigger);
                //spellEffects.addEffect(spellSpellID, thisEffect, chance);
            }
        } catch (Exception e) {
            System.out.println("[Talent] Failed to import spell effects for talent with ID " + spellID + " and rank " + rank);
            e.printStackTrace();
            return;
        }

    }

    public void loadSpellMod(DBReader reader) {

    }

    public int getSpellID() {
        return spellID;
    }

    public int getRank() {
        return rank;
    }
}
