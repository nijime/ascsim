/** GameState.java
 *
 * Class which stores information about one sim iteration (character, combatlog, loaded spells, ...)
 *
 *
 */


import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

public class GameState {
    private static final int seed = 1; // RNG seed
    private static final int NUMTARGETS = 1;

    private Character character;
    private ArrayList<Target> targets;

    private int curTarget;

    private CombatLog combatLog;
    private Summary summary;
    private SpellEffects spellEffects;
    private TriggerManager triggerManager;
    public TriggerManager getTriggers() { return triggerManager; }

    private double time;
    private double gcdEndTime;
    private double castEndTime;

    private PriorityQueue<CombatLogEvent> logQueue; // min PQ of "saved for later" CLEs
    private DOTCalc dotCalc;

    private Random rGen;

    public GameState(Character character) {
        this.character = character;
        this.character.setGameState(this);
        //this.spellEffects = spellEffects;
        this.triggerManager = new TriggerManager(this);

        this.time = 0;
        this.gcdEndTime = 0;
        this.castEndTime = 0;

        this.combatLog = new CombatLog(this);
        this.summary = new Summary();

        this.rGen = new Random(seed);

        this.targets = new ArrayList<Target>();
        targets.add(null);
        for (int i = 1; i <= NUMTARGETS; i++) {
            Target newTarg = new Target(i);
            newTarg.setGameState(this);
            targets.add(newTarg);

        }
        curTarget = 1;

        logQueue = new PriorityQueue<CombatLogEvent>();

        dotCalc = new DOTCalc(this);
    }

    /** curTargetID
     *
     * Returns the ID of the target currently being targeted
     *
     *
     */
    public int curTargetID() {
        return curTarget;
    }

    /** applyAuraByID
     *
     * Applies the aura with the specified spell ID to the entity with specified index
     *
     * @param auraID
     * @param targetID
     */
    public void applyAuraByID(int auraID, int targetID) {
        Aura parent = AuraManager.getAuraByID(auraID);

        if (parent == null) {
            System.out.println("Attempted to apply an aura with unknown ID");
            return;
        }

        AuraInstance auraToApply = null;

        switch (parent.getAuraType()) {
            case misc:
                auraToApply = new AuraInstance(parent, time(),time() + parent.getDuration());
                break;
            case periodic:
                auraToApply = new AuraPeriodicInstance((AuraPeriodic) parent, time(),time() + parent.getDuration(), this);
                dotCalc.track(targetID, parent.getID());
                break;
        }


        if (targetID == 0) {
            character.applyAura(auraToApply);
        } else {
            targets.get(targetID).applyAura(auraToApply);
        }
        // TODO cases for enemy targets
    }


    public SpellEffects getSpellEffects() {
        return spellEffects;
    }

    /** getTarget
     *
     * Returns the entity which has the given target index
     *
     * @param unitID
     * @return
     */
    public Target getTarget(int unitID) {
        if (unitID >= targets.size()) {
            return null;
        }

        return targets.get(unitID);
    }

    /** waitTillCast
     *
     * Pass time until the current cast is over
     *
     *
     */
    public void waitTillCast() {
        if (castEndTime > time) {
            dotCalc.calc(false, castEndTime);
            time = castEndTime;
        }

        logFromQueue();

        character.removeAuras(time);

        for (int i = 1; i <= NUMTARGETS; i++) {
            Target t = targets.get(i);
            t.removeAuras(time);
        }
    }

    /** waitTillGCD
     *
     * Pass time until the current GCD is over
     *
     *
     */
    public void waitTillGCD() {
        if (gcdEndTime > time) {
            dotCalc.calc(true, gcdEndTime);
            time = gcdEndTime;
        }

        logFromQueue();

        character.removeAuras(time);

        for (int i = 1; i <= NUMTARGETS; i++) {
            Target t = targets.get(i);
            t.removeAuras(time);
        }
    }

    public Character getPlayer() {
        return this.character;
    }

    public int getSpHash() {
        return character.getSpHash();
    }

    /** invokeCast
     *
     * Invokes a cast time which will last a given amount of time
     *
     * @param dt
     */
    public void invokeCast(double dt) {
        castEndTime = time() + dt;
    }

    /** invokeGCD
     *
     * Invokes a GCD which will last a given amount of time
     *
     * @param gcd
     */
    public void invokeGCD(double gcd) {
        gcdEndTime = time() + gcd;
    }

    public boolean onGCD() {
        return time() < gcdEndTime;
    }

    public boolean casting() {
        return time() < castEndTime;
    }

    public double time() {
        return time;
    }

    public double rand() {
        return rGen.nextDouble();
    }


    /** logEvent
     *
     * Sends the passed CLE to the combatlog, or waits to do so until the time given in the CLE has come
     *
     * @param cle
     */
    public void logEvent(CombatLogEvent cle) {
        if (cle.getTime() < time()) {
            System.out.println("Attempt to log an event back in time? The log is: \n    {" + cle.toString() + "}");
            return;
        }

        if (cle.getTime() == time()) {
            combatLog.logEvent(cle);
            summary.processEvent(cle);
        } else {
            logQueue.add(cle);
        }
    }

    /** logFromQueue
     *
     * Logs events which were saved to be logged later, which should be logged now
     *
     */
    private void logFromQueue() {
        CombatLogEvent peeked = logQueue.peek();

        while (peeked != null && peeked.getTime() <= time()) {
            CombatLogEvent cle = logQueue.poll();
            combatLog.logEvent(cle);
            summary.processEvent(cle);
            peeked = logQueue.peek();
        }
    }

    public Summary getSummary() {
        return summary;
    }

    public CombatLog getCombatLog() {
        return combatLog;
    }

}
