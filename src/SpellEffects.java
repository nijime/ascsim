/** SpellEffects.java   REPLACED BY TRIGGERS
 *
 *  TODO useless?
 *
 * Represents the contents of the SpellEffect table, which relates spells to effects which should occur upon a
 * successful cast
 *
 *
 */

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;

public class SpellEffects {

    private HashMap<Integer, ArrayList<Pair<Effect, Modify>>> effectMap;

    public SpellEffects() {
        effectMap = new HashMap<Integer, ArrayList<Pair<Effect, Modify>>>();
    }

    /** addEffect
     *
     * Add a cast effect to a given spell, with a given chance to occur
     *
     * @param spellID
     * @param effect
     * @param chance, a percentage (30.0 for 30%)
     */
    public void addEffect(int spellID, Effect effect, double chance) { // chance is a percentage (30.0 for 30%)
        Modify chanceMod = new Modify(chance);

        if (effectMap.containsKey(spellID)) {
            effectMap.get(spellID).add(new Pair<Effect, Modify>(effect, chanceMod));
        } else {
            ArrayList<Pair<Effect, Modify>> newList = new ArrayList<Pair<Effect, Modify>>();
            newList.add(new Pair<Effect, Modify>(effect, chanceMod));
            effectMap.put(spellID, newList);
        }
    }

    /** rollEffects
     *
     * For the given spell, roll for each effect attached to that spell whether it should happen
     *
     * Calls that effect's .execute() if it should
     *
     * @param spellID
     * @param gameState
     */
    public void rollEffects(int spellID, GameState gameState) {
        if (!effectMap.containsKey(spellID)) {
            // no effects to do
            return;
        }

        ArrayList<Pair<Effect, Modify>> effectList = effectMap.get(spellID);

        for (Pair<Effect, Modify> pair : effectList) {
            Effect effect = pair.getKey();
            double chance = pair.getValue().getDouble();

            boolean passed = gameState.rand() < chance/100.0;

            if (passed) {
                effect.execute(gameState);
            }
        }
    }
}
