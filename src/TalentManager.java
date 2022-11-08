/**
 *
 * Tracks which talents the character has learned. Talents may be considered constant over a simulation
 */

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

public class TalentManager {
    private HashMap<Pair<Integer, Integer>, Talent> talentMap;

    public TalentManager() {
        talentMap = new HashMap<Pair<Integer, Integer>, Talent>();
    }

    /** keepExists
     *
     * Removes entries from toLoad that are not recorded in the DB and imports their ID/rank
     * (filters out bad inputs which include invalid talents)
     *
     * @param reader
     * @param toLoad
     */
    private void keepExists(DBReader reader, HashSet<Pair<Integer, Integer>> toLoad) {
        HashSet<Pair<Integer, Integer>> finalSet = new HashSet<Pair<Integer, Integer>>(toLoad);

        for (Pair<Integer, Integer> pair : toLoad) {
            int ID = pair.getKey();
            int rank = pair.getValue();

            ResultSet result = reader.doQuery("SELECT * FROM talents.talent WHERE spell_id = '" + ID +
                    "' and talent_rank = '" + rank + "';");

            try {
                if (!result.next()) {
                    // talent not found
                    System.out.println("[TalentManager] No talent with ID " + ID + " and rank " + rank + " found");
                    continue;
                }

                finalSet.add(pair);

                Talent thisTalent = new Talent(ID, rank);
                talentMap.put(new Pair<Integer, Integer>(ID, rank), thisTalent);

            } catch (Exception e) {
                System.out.println("[TalentManager] Failed to import talents");
                return;
            }

        }

        // update toLoad
        toLoad = finalSet;
    }

    /** importTalents
     *
     * Given a list of talent:ranks, loads the effects of each talent
     *
     * @param reader
     * @param toLoad
     * @param gameState
     */
    public void importTalents(DBReader reader, HashSet<Pair<Integer, Integer>> toLoad, GameState gameState) {
        keepExists(reader, toLoad);

        for (Talent talent : talentMap.values()) {
            talent.loadSpellEffectTriggers(reader, gameState.getTriggers());
            talent.loadSpellMod(reader);
        }
        System.out.println("[TalentManager] Successfully imported " + String.valueOf(talentMap.size()) + " talents");
    }

}
