import java.util.LinkedList;

public class CombatLog {
    private static final int COMBATLOG_CAPACITY = 10;

    private LinkedList<CombatLogEvent> eventList;

    public CombatLog() {
        this.eventList = new LinkedList<CombatLogEvent>();
    }

    public void logEvent(CombatLogEvent cle) {
        eventList.addFirst(cle);
        if (eventList.size() >= COMBATLOG_CAPACITY) {
            eventList.removeLast();
        }

        System.out.println(String.valueOf(cle.getID()) + ", " + cle.getAmt() + ", " + String.valueOf(cle.getCrit()));
    }
}
