import java.util.*;
import java.util.concurrent.TimeUnit;

public class Model {
    public static class SpellDisplayRow implements Comparable<SpellDisplayRow> {
        private String name;
        private int damage;
        private double percent;
        private int casts;
        private int hits;
        private int misses;

        SpellDisplayRow(String name, int damage, double percent, int casts, int hits, int misses) {
            this.name = name;
            this.damage = damage;
            this.percent = percent;
            this.casts = casts;
            this.hits = hits;
            this.misses = misses;
        }

        public double getPercent() {
            return percent;
        }

        public int getCasts() {
            return casts;
        }

        public int getDamage() {
            return damage;
        }

        public int getHits() {
            return hits;
        }

        public int getMisses() {
            return misses;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(SpellDisplayRow o) {
            return this.damage < o.damage ? -1 : 1;
        }

        public String toString() {
            String toReturn = "    " + Utils.rightPad(name, 20) + " : " +
                    Utils.rightPad(damage + "", 6) + "| " +
                    Utils.rightPad(percent + "% ", 6) + "| " +
                    Utils.rightPad(casts + "", 6) + "| " +
                    Utils.rightPad(hits + "", 6) + "| " +
                    Utils.rightPad(misses + "", 6) + "";

            return toReturn;
        }
    }


    public static class AuraDisplayRow implements Comparable<AuraDisplayRow> {
        private String name;
        private double uptime;
        private double percent;
        private int count;

        AuraDisplayRow(String name, double uptime, double percent, int count) {
            this.name = name;
            this.uptime = uptime;
            this.percent = percent;
            this.count = count;
        }

        public double getPercent() {
            return percent;
        }

        public double getUptime() {
            return uptime;
        }

        public int getCount() {
            return count;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(AuraDisplayRow o) {
            return this.uptime < o.uptime ? -1 : 1;
        }

        public String toString() {
            String toReturn = "    " + Utils.rightPad(name, 20) + " : " +
                    Utils.rightPad(uptime + "s", 6) + "| " +
                    Utils.rightPad(percent + "% ", 6) + "| " +
                    count;

            return toReturn;
        }
    }


    private static Summary summary = null;

    /** setSummary
     *
     * Tells the model the results of a sim
     *
     *
     * @param simResult
     */
    public static void setSummary(Summary simResult) {
        summary = simResult;
    }

    /** ready
     *
     * Returns whether a sim has been run and the Model contains data to be displayed
     *
     * @return
     */
    public static boolean ready() {
        return summary != null;
    }

    /** getDPS
     *
     * Returns the overall DPS value
     *
     * @return
     */
    public static int getDPS() {
        if (!ready()) {
            System.out.println("Sim has not been run yet");
            System.exit(-1);
        }

        return summary.getDPS();
    }

    /** getSpellDisplay
     *
     * Returns an ArrayList of SpellDisplayRows, each of which represents a row that should be displayed in the results tab
     *
     *
     * @return
     */
    public static ArrayList<SpellDisplayRow> getSpellDisplay() {
        if (!ready()) {
            System.out.println("Sim has not been run yet");
            System.exit(-1);
        }

        Set<Integer> spellIDs = summary.getSpellSet();
        ArrayList<SpellDisplayRow> spellRows = new ArrayList<SpellDisplayRow>();

        for (int ID : spellIDs) {
            String name = SpellManager.getName(ID);
            int damage = summary.getSpellDamage(ID);
            double percent = summary.getSpellDamagePercent(ID);
            int casts = summary.getNumCasts(ID);
            int hits = summary.getNumHits(ID);
            int misses = summary.getNumMisses (ID);
            SpellDisplayRow thisRow = new SpellDisplayRow(name, damage, percent, casts, hits, misses);
            spellRows.add(thisRow);
        }

        Collections.sort(spellRows, Collections.reverseOrder());

        return spellRows;
    }


    /** getAuraDisplay
     *
     * Returns an ArrayList of AuraDisplayRows, each of which represents a row that should be displayed in the results tab
     *
     *
     * @return
     */
    public static ArrayList<AuraDisplayRow> getAuraDisplay() {
        if (!ready()) {
            System.out.println("Sim has not been run yet");
            System.exit(-1);
        }

        Set<Integer> spellIDs = summary.getAuraSet();
        ArrayList<AuraDisplayRow> auraRows = new ArrayList<AuraDisplayRow>();

        for (int ID : spellIDs) {
            String name = AuraManager.getName(ID);
            double uptime = summary.getUptime(ID);
            double percent = summary.getUptimePercent(ID);
            int count = summary.getAuraApplications(ID);
            AuraDisplayRow thisRow = new AuraDisplayRow(name, uptime, percent, count);
            auraRows.add(thisRow);
        }

        Collections.sort(auraRows, Collections.reverseOrder());

        return auraRows;
    }

    public static int getExecutionTime() {
        return (int) TimeUnit.NANOSECONDS.toMillis(summary.getExecutionTime());
    }

}
