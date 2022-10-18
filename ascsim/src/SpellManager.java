import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SpellManager {

    private static HashMap<Integer, String> nameMap = new HashMap<Integer, String>();

    public static String getName(int ID) {
        return nameMap.get(ID);
    }

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

                Spell thisSpell = new Spell(ID, result, reader);
                spellList.put(ID, thisSpell);
            }

            System.out.println("[SpellManager] Successfully imported " + String.valueOf(spellList.size()) + " spells");
        } catch (Exception e) {
            System.out.println("[SpellManager] Failed to retrieve spell data");
            return;
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean canCastSpell(int ID) {
        return spellList.get(ID).canCast(gameState);
    }

    public Event cast(int ID) {
        if (gameState == null) {
            System.out.println("[SpellManager] Assign a gamestate to this spell manager");
            return null;
        }

        boolean canCast = !gameState.onGCD() && !gameState.casting();

        // check whether can cast in a different way
        if (!canCast) {
            System.out.println("[SpellManager] Attempted to cast a spell while casting was not possible");
            System.exit(-1);
        }

        Spell toCast = spellList.get(ID);
        return toCast.cast(gameState);
    }

   /* public Event cast(int ID, GameState gameState) {
        Spell toCast = spellList.get(ID);
        return toCast.cast(gameState);
    }*/




}
