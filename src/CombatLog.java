import java.util.LinkedList;

public class CombatLog {
    private static final int COMBATLOG_CAPACITY = 30;
    public static final boolean PRINT_EVENTS = true;

    private LinkedList<CombatLogEvent> eventList;
    private GameState gameState;

    private double lastTime;

    public CombatLog(GameState gameState) {
        this.eventList = new LinkedList<CombatLogEvent>();
        this.gameState = gameState;

        this.lastTime = 0.0;

    }

    /** logEvent
     *
     * Adds the passed event to the combatlog and postprocesses it
     *
     * @param cle
     */
    public void logEvent(CombatLogEvent cle) {
        eventList.addFirst(cle);
        if (eventList.size() >= COMBATLOG_CAPACITY) {
            eventList.removeLast();
        }

        if (cle.getTime() != lastTime && PRINT_EVENTS) {
            System.out.println();
        }

        lastTime = cle.getTime();
        if (PRINT_EVENTS) {
            System.out.println(cle.toString());
        }

        processPost(cle);
    }

    /** processPost
     *
     * Responds to cles as required, ex. procs on SPELL_DAMAGE
     *
     *
     * @param cle
     */
    private void processPost(CombatLogEvent cle) {

        // triggers are listening
        gameState.getTriggers().process(cle);

        switch (cle.getSubEvent()) {
            case "SPELL_CAST_SUCCESS":
                // remove auras if an aura was used to allow this spell's casting
                int auraToRemove = cle.getParam(5); // suffix 4
                if (auraToRemove != -1) {
                    gameState.getPlayer().removeBuffByID(auraToRemove);
                }
                break;
            default:
                // do nothing
                break;
        }
    }

}
