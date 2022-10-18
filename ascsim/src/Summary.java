import java.util.HashMap;

public class Summary {

    private int totalDamage;
    private double timePassed;

    private HashMap<Integer, Integer> casts;

    public Summary() {
        totalDamage = 0;
        timePassed = 0;

        casts = new HashMap<Integer, Integer>();
    }

    public void processEvent(CombatLogEvent cle) {
        totalDamage += cle.getAmt();
        timePassed = cle.getTime();

        if (casts.containsKey(cle.getID())) {
            int cur = casts.get(cle.getID());
            casts.put(cle.getID(), cur+1);
        } else {
            casts.put(cle.getID(), 1);
        }
    }

    public void processPost(GameState gameState) {
        // empty
        //timePassed = gameState.time();
    }

    public int getDPS() {
        return (int) ((double) totalDamage / (double) timePassed);
    }

    public int getNumCasts(int ID) {
        if (!casts.containsKey(ID)) {
            return 0;
        }

        return casts.get(ID);
    }
}
