import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class TriggerManager {

    // separate registered triggers by subevent so you only have to iterate over part of the triggers for each CLE
    private HashMap<Integer, ArrayList<CLETrigger>> registered;
    private GameState gameState;

    public TriggerManager(GameState gameState) {
        this.gameState = gameState;

        this.registered = new HashMap<Integer, ArrayList<CLETrigger>>();
    }

    /** register
     *
     * Adds a trigger to be checked on combat log event
     *
     */
    public void register(CLETrigger trigger) {
        int subEventInt = trigger.getEvent().getSubEventInt();
        if (registered.containsKey(subEventInt)) {
            registered.get(subEventInt).add(trigger);
        } else {
            ArrayList<CLETrigger> newArr = new ArrayList<CLETrigger>();
            newArr.add(trigger);
            registered.put(subEventInt, newArr);
        }
    }

    /** process
     *
     * Checks for the activation of triggers which this CLE would satisfy
     *
     * @param cle
     */
    public void process(CombatLogEvent cle) {

        if (!registered.containsKey(cle.getSubEventInt())) {
            return; // no triggers registered for this subevent
        }

        ArrayList<CLETrigger> bySubEvent = registered.get(cle.getSubEventInt());

        for (CLETrigger trigger : bySubEvent) {
            if (trigger.getEvent().describes(cle) && trigger.conditionsMet(gameState)) {
                trigger.fire(gameState);
            }
        }
    }

}
