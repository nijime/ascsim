/** Utils.java
 *
 * General methods and enums to be used by multiple classes
 *
 */

public class Utils {
    public enum MainStat {
        strength(0), agility(1), intellect(2), spirit(3);

        private int val;

        MainStat(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }


    public enum School {
        physical(0), holy(1), frost(2), shadow(3), nature(4), fire(5), arcane(6),
        // extended schools
        froststorm(7), // nature and frost
        divine(8), // arcane and holy
        elemental(9), // nature, fire, frost
        astral(10), // arcane and nature
        spellfire(11), // arcane and fire
        twilight(12), // shadow and holy
        plague(13), // shadow and nature
        radiant(14); // fire and holy


        private int val;

        School(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }

    }

    public static int merge(int spellType, int school) {
        return 10*school + spellType;
    }

    public static int merge(Spell.SpellType spellType, School school) {
        return merge(spellType.asInt(), school.asInt());
    }


    private static int debugCounter = 0;

    public static void debugCount() {
        debugCounter++;
        System.out.println(debugCounter);
    }

    public static String rightPad(String s, int n) {
        if (s.length() >= n) {
            return s.substring(0, n);
        }

        String pad = "";
        for (int i = 0; i < n - s.length(); i++) {
            pad += " ";
        }

        return s + pad;
    }

    public static String rightPad(int s, int n) {
        return rightPad(String.valueOf(s), n);
    }

    public static String rightPad(double s, int n) {
        return rightPad(String.valueOf(s), n);
    }

    public static String rightPad(Integer s, int n) {
        return rightPad(String.valueOf(s), n);
    }

    public static String rightPad(Double s, int n) {
        return rightPad(String.valueOf(s), n);
    }

}
