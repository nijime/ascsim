import java.util.ArrayList;

/** CLETrigger.java
 *
 * Checks whether certain conditions have been met by a combat log event and takes any appropriate actions (ex. proc on
 * spell damage, ...)
 *
 */

public class CLETrigger {

    private CLETemplate event;
    private Effect action;

    private Modify chance;
    /*
     * null = no condition
     *
     * > 100 = has specified aura
     *
     *
     */
    private ArrayList<Integer> conditions;

    public CLETrigger(CLETemplate event, Effect action) {
        this.event = event;
        this.action = action;

        this.chance = new Modify(100.0);

        conditions = new ArrayList<Integer>();
    }

    /** setChance
     *
     * Set the chance that this trigger fires, given as:
     *
     * 100.0 = 100%
     * 0.0 = 0%
     *
     * @param chance
     */
    public void setChance(double chance) {
        this.chance = new Modify(chance);
    }

    /**
     *
     * Adds a condition which must be met for the trigger to be fired
     *
     * 100 -> has aura with this ID
     *
     * @param condition
     */
    public void addCondition(int condition) {
        conditions.add(condition);
    }

    public CLETemplate getEvent() {
        return event;
    }

    public boolean conditionsMet(GameState gameState) {
        for (int condition : conditions) {
            if (condition > 100) { // check has aura conditions
                if (!gameState.getPlayer().hasAura(condition)) {
                    return false;
                }
            }

            if (condition == 0) { // tbd

            }
        }

        return true;
    }

    public void fire(GameState gameState) {
        boolean passed = gameState.rand() < chance.getDouble()/100.0;

        if (passed) {
            action.execute(gameState);

        }

    }
}
