/** DOTCalc.java
 *
 * Creates DoT tick and melee swing CLEs
 *
 * TODO add character MH/OFF swingtimes, then add swing calculations to this
 *
 * Assumes all DoTs are debuffs on enemy targets
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class DOTCalc {
    private final static int MAXTARGETS = 10;// TODO 10 is hardcoded max targets

    GameState gameState;
    //HashMap<Integer, Integer> tracked;
    ArrayList<HashSet<Integer>> tracked;

    double prevTime; // time when last called

    public DOTCalc(GameState gameState) {
        // pass gamestate to automatically track all Entitys
        this.gameState = gameState;

        tracked = new ArrayList<HashSet<Integer>>();
        for (int i = 0; i < MAXTARGETS; i++) {
            tracked.add(new HashSet<Integer>());
        }

        prevTime = 0.0;
    }

    /** track
     *
     * Tells the dotcalc to start creating CLEs for the specified aura
     * auraID must be the ID of a periodic aura
     *
     * @param targetID
     * @param auraID
     */
    public void track(int targetID, int auraID) {
        if(targetID > MAXTARGETS) {
            System.out.println("[DOTCalc] Target ID is greater than number of targets");
            return;
        }

        if (AuraManager.getAuraByID(auraID).getAuraType() != Aura.AuraType.periodic) {
            System.out.println("[DOTCalc] Can't track a non-periodic aura");
            return;
        }

        tracked.get(targetID).add(auraID);
    }

    /** calc
     *
     * Creates CLEs for the time between prevTime and passed curTime
     * if the player is casting for the duration of that interval, pass canSwing = false
     *
     * curTime is the time that the gamestate will be in after the time is advanced due to waitTill..
     *
     * @param canSwing
     */
    public void calc(boolean canSwing, double curTime) {
        //double curTime = gameState.time();

        if (prevTime == curTime) {
            // no time has passed
            return;
        }

        if (canSwing) {
            // TODO melee swings
        }



        for (int i = 0; i < tracked.size(); i++) {
            Target thisTarget = gameState.getTarget(i);
            HashSet<Integer> auraIDs = tracked.get(i);
            for (int auraID : auraIDs) {
                AuraPeriodicInstance thisInstance = (AuraPeriodicInstance) thisTarget.getDebuffByID(auraID);
                if (thisInstance == null) {
                    // debuff not on target
                    return;
                }
                double tickTime = thisInstance.getTickTime();

                // assume ticks happen at tickTime, tickTime*2, ..., duration
                // (no tick on application)
                // assume no pandemic, aura refresh resets tick timer (this is the case for SWP)

                if (prevTime < thisInstance.getApplicationTime()) {
                    // last calculation was before this aura was applied
                    if (curTime < thisInstance.getApplicationTime() + tickTime) {
                        // no tick should have happened yet
                    } else {
                        int ticksToDo = (int) ((curTime - thisInstance.getApplicationTime()) / tickTime);

                        // do ticks
                        for (int j = 1; j <= ticksToDo; j++) {
                            Utils.School auraSchool = thisInstance.getSchool();
                            CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL_PERIODIC, auraID, auraSchool.asInt());
                            CombatLogEvent.SuffixParams sp1 = thisInstance.tickDamage();
                            double tickOn = thisInstance.getApplicationTime() + tickTime * j; // the gamestate time this tick should occur on
                            CombatLogEvent cle_periodic = new CombatLogEvent(tickOn, pp1, sp1, 0, i);
                            gameState.logEvent(cle_periodic);
                        }

                    }
                } else {
                    double prevProg = (prevTime - thisInstance.getApplicationTime()) % tickTime; // previous time progressed as of prevTime
                    double timePassed = curTime - prevTime;
                    int ticksToDo = (int) ((prevProg + timePassed) / tickTime);

                    // do ticks
                    for (int j = 0; j < ticksToDo; j++) {

                        Utils.School auraSchool = thisInstance.getSchool();
                        CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL_PERIODIC, auraID, auraSchool.asInt());
                        CombatLogEvent.SuffixParams sp1 = thisInstance.tickDamage();
                        double tickOn = prevTime + (tickTime - prevProg) + tickTime * j; // the gamestate time this tick should occur on
                        CombatLogEvent cle_periodic = new CombatLogEvent(tickOn, pp1, sp1, 0, i);
                        gameState.logEvent(cle_periodic);
                    }
                }

            }
        }

        prevTime = curTime;
    }
}
