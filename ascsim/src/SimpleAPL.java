/** SimpleAPL.java
 *
 * A list of spells to cast in order of priority. If the first can't be cast, attempt to cast the second, etc.
 *
 */

import java.util.ArrayList;

public class SimpleAPL {

    private ArrayList<Integer> prioList;

    public SimpleAPL() {
        this.prioList = new ArrayList<Integer>();
        //prioList.add(27180, 25364);
    }

    public void addItem(int ID) {
        prioList.add(ID);
    }

    /** getNext
     *
     * returns the ID of the next spell to cast
     *
     * @param gameState
     * @param spellManager
     * @return
     */
    public int getNext(GameState gameState, SpellManager spellManager) {

        int i = 0;
        while (i < prioList.size()) {
            int ID = prioList.get(i);

            Target curTarget = gameState.getTarget(gameState.curTargetID());

            if (spellManager.canCastSpell(ID) && !curTarget.hasAura(ID)) { // TODO hardcoded to not cast a spell if target has same-ID aura already
                return ID;
            }

            i++;
        }

        return -1;
    }

    public ArrayList<Integer> getPrioList() {
        return prioList;
    }

}
