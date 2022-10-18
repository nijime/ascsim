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

    public int getNext(GameState gameState, SpellManager spellManager) { // returns the ID of the next spell to cast

        int i = 0;
        while (i < prioList.size()) {
            int ID = prioList.get(i);

            if (spellManager.canCastSpell(ID)) {
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
