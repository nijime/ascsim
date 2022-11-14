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

    /**
     * Return a list of integers representing every spellID listed in the APL string
     *
     * @return
     */
    public ArrayList<Integer> getSpellList() {
        return prioList;
    }


    /**
     *
     * Convert APL string to logic
     *
     *
     * @param aplString
     */
    public void fromStr(String aplString) {
        ArrayList<Integer> IDList = APLStrToList(aplString);

        if (IDList == null) {
            System.out.println("Couldn't import APL from string");
            System.exit(-1);
        }

        for (int ID : IDList) {
            this.addItem(ID);
        }
    }

    /** APLStrToList
     *
     * Converts APL string to a list of integers, returns null if APL string is deformed
     *
     * @param aplString
     * @return
     */
    private static ArrayList<Integer> APLStrToList(String aplString) {
        String noSpace = aplString.replace(" ", "");
        String[] splt = noSpace.split(",");
        ArrayList<Integer> toReturn = new ArrayList<Integer>();

        for (int i = 0; i < splt.length; i++) {
            int asInt;
            try {
                asInt = Integer.parseInt(splt[i]);
            } catch (Exception e) {
                // invalid APL string
                return null;
            }

            toReturn.add(asInt);
        }

        return toReturn;
    }

}
