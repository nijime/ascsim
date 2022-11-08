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

        ResultSet result = reader.doQuery("SELECT * FROM talents.talentspelleffect " +
                " WHERE spell_id = '" + spellID + "' " +
                " and talent_rank = '" + rank + "' " +
                ";");

        try {
            while (result.next()) {
                int spellSpellID = result.getInt("spell_spell_id");
                String effectName = result.getString("effect_name");
                double magnitude = result.getDouble("magnitude"); // not used for apply_aura
                String args = result.getString("args");
                double chance = result.getDouble("chance");

                Effect thisEffect = null;

                int auraToApply = Integer.parseInt(args);

                switch (effectName.toLowerCase()) {
                    case "apply_aura_to_target":
                        thisEffect = new EffectApplyAura(false, auraToApply); // TODO currently only allowing independent procs of single effects (separate args out, use array?)
                        AuraManager.loadAuraByID(reader, auraToApply); // load necessary auras as we find them
                        break;
                    case "apply_aura_to_caster":
                        thisEffect = new EffectApplyAura(true, Integer.parseInt(args)); // TODO currently only allowing independent procs of single effects (separate args out, use array?)
                        AuraManager.loadAuraByID(reader, auraToApply);
                        break;
                    default:
                        // do nothing
                        break;
                }

                CLETemplate template = new CLETemplate(CLEDescriptor.Prefix.SPELL, CLEDescriptor.Suffix.CAST_SUCCESS);
                template.watchParamIndex(0, spellSpellID); // watch for matching spell ID

                CLETrigger trigger = new CLETrigger(template, thisEffect);
                trigger.setChance(chance);

                triggerManager.register(trigger);
                //spellEffects.addEffect(spellSpellID, thisEffect, chance);
            }
        } catch (Exception e) {
            System.out.println("[Talent] Failed to import spell effects for talent with ID " + spellID + " and rank " + rank);
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
