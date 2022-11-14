/** Entity.java
 *
 * Parent class to Character and Target
 *
 *
 */

import java.util.HashMap;

public abstract class Entity {


    protected GameState gameState;

    protected HashMap<Integer, AuraInstance> buffs;
    protected HashMap<Integer, AuraInstance> debuffs;

    public Entity() {
        this.buffs = new HashMap<Integer, AuraInstance>();
        this.debuffs = new HashMap<Integer, AuraInstance>();

    }

    /** setGameState
     *
     * Save the gameState this character is a part of, for using later
     *
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /** removeAuras
     *
     * Removes buffs / debuffs if the passed value is greater than the aura's expiration time
     *
     * @param time
     */
    public void removeAuras(double time) {
        for (int buffID : buffs.keySet()) {
            AuraInstance auraInstance = buffs.get(buffID);
            if (auraInstance.getExpirationTime() <= time) {
                removeBuffByID(buffID);
            }
        }

        for (int debuffID : debuffs.keySet()) {
            AuraInstance auraInstance = debuffs.get(debuffID);
            if (auraInstance.getExpirationTime() <= time) {
                removeDebuffByID(debuffID);
            }
        }
    }

    /** removeBuffByID
     *
     * Removes the buff with specified ID and reports it as occurring at the given time
     *
     * @param ID
     * @param time
     */
    public void removeBuffByID(int ID, double time) {
        if (!buffs.containsKey(ID)) {
            return;
        }

        AuraInstance toRemove = buffs.get(ID);

        CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, ID, toRemove.getSchool().asInt());
        CombatLogEvent.SuffixParams sp1 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.AURA_REMOVED, -1, -1, -1);
        CombatLogEvent cle_spell_aura_removed = new CombatLogEvent(time, pp1, sp1, 0, 0);

        buffs.remove(ID);

        gameState.logEvent(cle_spell_aura_removed);
    }

    /** removeBuffByID
     *
     * Removes the buff with the specified ID (at the current time)
     *
     * @param ID
     */
    public void removeBuffByID(int ID) {
        removeBuffByID(ID, gameState.time());
    }

    /** removeDebuffByID
     *
     * Removes the debuff with the specified ID (at the current time)
     *
     * @param ID
     */
    public void removeDebuffByID(int ID) {
        if (!debuffs.containsKey(ID)) {
            return;
        }

        AuraInstance toRemove = debuffs.get(ID);

        CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, ID, toRemove.getSchool().asInt());
        CombatLogEvent.SuffixParams sp1 = new CombatLogEvent.SuffixParams(CombatLogEvent.Suffix.AURA_REMOVED, -1, -1, -1);
        CombatLogEvent cle_spell_aura_removed = new CombatLogEvent(gameState.time(), pp1, sp1, 0, 0);

        debuffs.remove(ID);

        gameState.logEvent(cle_spell_aura_removed);
    }

    /** applyAura
     *
     * Applies the passed aura instance to the character
     *
     * @param auraToApply
     */
    public void applyAura(AuraInstance auraToApply) {
        if (auraToApply.isHarmful()) {
            debuffs.put(auraToApply.getID(), auraToApply);
        } else {
            buffs.put(auraToApply.getID(), auraToApply);
        }
    }

    /** refreshAura
     *
     * Refreshes the aura instance to base duration which is based off the passed Aura
     *
     * @param auraParent
     */
    public void refreshAura(Aura auraParent) {
        if (auraParent.isHarmful()) {
            if (!debuffs.containsKey(auraParent.getID())) {
                return;
            }
            debuffs.get(auraParent.getID()).refreshDuration(gameState.time());
        } else {
            if (!buffs.containsKey(auraParent.getID())) {
                return;
            }
            buffs.get(auraParent.getID()).refreshDuration(gameState.time());
        }
    }

    /** hasAura
     *
     * Returns whether the character has the aura with given ID
     *
     * @param auraID
     * @return
     */
    public boolean hasAura(int auraID) {
        if (buffs.containsKey(auraID) || debuffs.containsKey(auraID)) {
            return true;
        }

        return false;
    }

    /** getDebuffByID
     *
     * returns the debuff AuraInstance, if this entity has it
     *
     *
     * @param auraID
     * @return
     */
    public AuraInstance getDebuffByID(int auraID) {
        if (debuffs.containsKey(auraID)) {
            return debuffs.get(auraID);
        }

        return null;
    }

}
