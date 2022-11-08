/** SpellManager.java
 *
 * Imports Spells from the database and saves references to the populated Spell objects
 *
 */

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

public class SpellManager {

    private static HashMap<Integer, String> nameMap = new HashMap<Integer, String>();

    private HashMap<Integer, Spell> spellList;
    private GameState gameState;

    public SpellManager(GameState gameState) {
        this.spellList = new HashMap<Integer, Spell>();
        this.gameState = gameState;
    }

    public void importSpells(DBReader reader, HashSet<Integer> toLoad) {
        HashMap<Integer, Spell> spellMap = new HashMap<Integer, Spell>();

        ResultSet result = reader.retrieveRecords("[spells].[offensive]");

        try {
            while (result.next()) {
                int ID = result.getInt("spell_id");
                String name = result.getString("spell_name");
                nameMap.put(ID, name);

                if (!toLoad.contains(ID)) {
                    continue;
                }

                Spell thisSpell = new Spell(ID, result, reader, gameState);
                spellList.put(ID, thisSpell);
            }

            System.out.println("[SpellManager] Successfully imported " + String.valueOf(spellList.size()) + " spells");
        } catch (Exception e) {
            System.out.println("[SpellManager] Failed to retrieve spell data");
            return;
        }
    }

    /** getName
     *
     * Returns the name of the spell with given ID
     *
     * @param ID
     * @return
     */
    public static String getName(int ID) {
        return nameMap.get(ID);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /** canCastSpell
     *
     * Returns whether the spell with the given ID can be cast at the current time
     *
     * @param ID
     * @return
     */
    public boolean canCastSpell(int ID) {
        return spellList.get(ID).canCast(gameState);
    }

    /** cast
     *
     * Casts the spell with the given ID, if the player is not currently busy
     *
     * @param ID
     */
    public void cast(int ID) {
        if (gameState == null) {
            System.out.println("[SpellManager] Assign a gamestate to this spell manager");
            return;
        }

        boolean canCast = !gameState.onGCD() && !gameState.casting();

        // check whether can cast in a different way
        if (!canCast) {
            System.out.println("[SpellManager] Attempted to cast a spell while casting was not possible");
            System.exit(-1);
        }

        Spell toCast = spellList.get(ID);
        toCast.cast(gameState);
        //return toCast.cast(gameState);
    }
}
