/** Summary.java
 *
 * Calculates statistics for one sim iteration (Details)
 *
 */

import java.util.HashMap;
import java.util.Set;

public class Summary {

    private class SpellInfo {
        private int ID;
        private int casts;
        private int damage;
        private int hits;
        private int misses;
        // damage
        // min
        // max
        // min crit
        // max crit

        public SpellInfo(int ID) {
            this.ID = ID;
            this.casts = 1; // class should not be instantiated until the spell is cast once
            this.damage = 0;
            this.hits = 0;
            this.misses = 0;
        }

        public void addDamage(int dmg) {
            this.damage += dmg;
        }

        public void setCasts(int casts) {
            this.casts = casts;
        }

        public int getCasts() {
            return casts;
        }

        public void addHit() {
            this.hits += 1;
        }

        public int getHits() {
            return hits;
        }

        public void addMiss() {
            this.misses += 1;
        }

        public int getMisses() {
            return misses;
        }

        public int getDamage() {
            return damage;
        }

        public boolean equals(SpellInfo other) {
            return this.ID == other.ID;
        }

        public int hashCode() {
            return ID;
        }
    }

    private class AuraInfo {
        private int ID;
        private double uptime;
        private int applications;
        private double timeLastApplied;
        // damage
        // min
        // max
        // min crit
        // max crit

        public AuraInfo(int ID) {
            this.ID = ID;
            this.uptime = 0.0;
            this.applications = 1; // class should not be instantiated until the aura is applied once
            this.timeLastApplied = -1.0;
        }



        public void setUptime(double uptime) {
            this.uptime = uptime;
        }
        public double getUptime() {
            return uptime;
        }

        public void setApplications(int applications) {
            this.applications = applications;
        }
        public int getApplications() {
            return applications;
        }

        public void setTimeLastApplied(double timeLastApplied) {
            this.timeLastApplied = timeLastApplied;
        }
        public double getTimeLastApplied() {
            return timeLastApplied;
        }

        public boolean equals(AuraInfo other) {
            return this.ID == other.ID;
        }

        public int hashCode() {
            return ID;
        }
    }



    private int totalDamage;
    private double timePassed;

    private boolean endStateChecked;

    private HashMap<Integer, SpellInfo> spells;
    private HashMap<Integer, AuraInfo> auras;

    long start;
    long end;

    public Summary() {
        start = 0;
        end = 0;

        totalDamage = 0;
        timePassed = 0;

        endStateChecked = false;

        spells = new HashMap<Integer, SpellInfo>();
        auras = new HashMap<Integer, AuraInfo>();

    }

    /** processEvent
     *
     * From the given event, updates counters and total damage, etc. as necessary
     *
     *
     * @param cle
     */
    public void processEvent(CombatLogEvent cle) {
        endStateChecked = false;

        double time = cle.getTime();
        int param0 = cle.getParam(0); // Spell ID

        switch (cle.getSubEvent()) {
            case "SPELL_DAMAGE":
            case "SPELL_PERIODIC_DAMAGE":
                totalDamage += cle.getParam(2);
                timePassed = time;

                if (spells.containsKey(param0)) {
                    spells.get(param0).addDamage(cle.getParam(2));
                    spells.get(param0).addHit();
                } else {
                    System.out.println("[Summary] Damage event before cast success?");
                    System.exit(-1);
                }

                break;
            case "SPELL_CAST_SUCCESS":
                if (spells.containsKey(param0)) {
                    SpellInfo info = spells.get(param0);
                    int cur = info.getCasts();
                    info.setCasts(cur+1);
                } else {
                    SpellInfo newInfo = new SpellInfo(param0);
                    spells.put(param0, newInfo);
                }

                break;
            case "SPELL_MISSED":
                if (spells.containsKey(param0)) {
                    spells.get(param0).addMiss();
                } else {
                    System.out.println("[Summary] Miss event before cast success?");
                    System.exit(-1);
                }
                break;
            case "SPELL_AURA_APPLIED":
                if (auras.containsKey(param0)) {
                    AuraInfo info = auras.get(param0);

                    info.setTimeLastApplied(time);

                    int curApplications = info.getApplications();
                    info.setApplications(curApplications+1);
                } else {
                    AuraInfo newInfo = new AuraInfo(param0);
                    newInfo.setTimeLastApplied(time);
                    auras.put(param0, newInfo);
                }

                break;
            case "SPELL_AURA_REMOVED":
                if (auras.containsKey(param0)) {
                    AuraInfo info = auras.get(param0);

                    double prevUptime = info.getUptime();
                    info.setUptime(prevUptime + (time - info.getTimeLastApplied()));

                    info.setTimeLastApplied(-1.0);
                } else {
                    // should never occur
                    auras.put(param0, new AuraInfo(param0));
                }

                break;
            default:
                // nothing
                break;
        }

    }

    /** checkEndState
     *
     * Necessary minor adjustments when the sim ends abruptly, ex. to add current buffs to uptime values
     *
     */
    private void checkEndState() {
        for (int auraID : auras.keySet()) {
            AuraInfo info = auras.get(auraID);
            if (info.getTimeLastApplied() != -1.0) {
                // add current to uptime

                double prevUptime = info.getUptime();
                info.setUptime(prevUptime + (timePassed - info.getTimeLastApplied()));

                info.setTimeLastApplied(-1.0);
            }
        }


        endStateChecked = true;
    }

    public int getDPS() {
        if (!endStateChecked) {
            checkEndState();
        }

        return (int) ((double) totalDamage / (double) timePassed);
    }

    public int getNumCasts(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!spells.containsKey(ID)) {
            return 0;
        }

        return spells.get(ID).getCasts();
    }

    public int getNumMisses(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!spells.containsKey(ID)) {
            return 0;
        }

        return spells.get(ID).getMisses();
    }

    public int getNumHits(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!spells.containsKey(ID)) {
            return 0;
        }

        return spells.get(ID).getHits();
    }

    public int getSpellDamage(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!spells.containsKey(ID)) {
            return 0;
        }

        return spells.get(ID).getDamage();
    }

    public double getSpellDamagePercent(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!spells.containsKey(ID)) {
            return 0.0;
        }

        return Math.round(((double) spells.get(ID).getDamage() / (double) totalDamage) * 1000.0) / 10.0;
    }

    public Set<Integer> getSpellSet() {
        return spells.keySet();
    }

    public Set<Integer> getAuraSet() {
        return auras.keySet();
    }

    public double getUptime(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!auras.containsKey(ID)) {
            return 0.0;
        }

        return Math.round(auras.get(ID).getUptime()*10.0)/10.0;
    }

    public double getUptimePercent(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!auras.containsKey(ID)) {
            return 0.0;
        }

        return Math.round((auras.get(ID).getUptime() / timePassed) * 1000.0) / 10.0;
    }

    public int getAuraApplications(int ID) {
        if (!endStateChecked) {
            checkEndState();
        }

        if (!auras.containsKey(ID)) {
            return 0;
        }

        return auras.get(ID).getApplications();
    }


    public void startTime() {
        this.start = System.nanoTime();
    }

    public void endTime() {
        this.end = System.nanoTime();
    }

    public long getExecutionTime() {
        return end - start;
    }
}
