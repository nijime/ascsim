import java.util.ArrayList;

public abstract class CLEDescriptor {
    public enum Prefix {
        SPELL(0), SPELL_PERIODIC(1);

        private int val;

        Prefix(int val) {
            this.val = val;
        }

        public static Prefix fromStr(String val) {
            switch (val.toLowerCase()) {
                case "spell":
                    return SPELL;
                case "spell_periodic":
                    return SPELL_PERIODIC;
                default:
                    return SPELL;
            }
        }

        public String asStr() {
            switch (val) {
                case 0:
                    return "SPELL";
                case 1:
                    return "SPELL_PERIODIC";
                default:
                    return "SPELL";
            }
        }

        public int asInt() {
            return val;
        }
    }

    public enum Suffix {
        DAMAGE(0), MISSED(1), AURA_APPLIED(2), AURA_REMOVED(3), AURA_REFRESH(4), CAST_SUCCESS(5),
        CAST_START(6);

        private int val;

        Suffix(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }

        public String asStr() {
            switch (val) {
                case 0:
                    return "DAMAGE";
                case 1:
                    return "MISSED";
                case 2:
                    return "AURA_APPLIED";
                case 3:
                    return "AURA_REMOVED";
                case 4:
                    return "AURA_REFRESH";
                case 5:
                    return "CAST_SUCCESS";
                case 6:
                    return "CAST_START";
                default:
                    return "DAMAGE";
            }
        }

        public static Suffix fromStr(String val) {
            switch (val.toLowerCase()) {
                case "damage":
                    return DAMAGE;
                case "missed":
                    return MISSED;
                case "aura_applied":
                    return AURA_APPLIED;
                case "aura_removed":
                    return AURA_REMOVED;
                case "aura_refresh":
                    return AURA_REFRESH;
                case "cast_success":
                    return CAST_SUCCESS;
                case "cast_start":
                    return CAST_START;
                default:
                    return DAMAGE;
            }
        }
    }

    public static class PrefixParams {
        /**
         * Parameters determined by the prefix
         * local # | global # |   SPELL   |
         *      0       0        spellID
         *      1       1        school
         */

        public final static int COUNT = 2;

        protected Prefix prefix;
        protected int spellID;
        protected int spellSchool;

        public PrefixParams(Prefix prefix, int spellID, int spellSchool) {
            this.prefix = prefix;

            this.spellID = spellID;
            this.spellSchool = spellSchool;
        }
    }

    public static class SuffixParams {
        /**
         * Parameters determined by the suffix
         * local # | global # |   DAMAGE   |  SUCCESS  | AURA_REMOVED
         *      1       2        amount          -            -
         *      2       3        school          -            -
         *      3       4        -               -            -
         *      4       5        -         aura to remove     -
         */

        public final static int COUNT = 4;

        protected Suffix suffix;
        protected int param1;
        protected int param2;
        protected int param3;
        protected int param4; // aura to remove (spell cast success)

        public SuffixParams(Suffix suffix, int param1, int param2, int param3) {
            this.suffix = suffix;

            this.param1 = param1;
            this.param2 = param2;
            this.param3 = param3;

            this.param4 = -1;
        }

        public SuffixParams(Suffix suffix, int param1, int param2, int param3, int param4) {
            this(suffix, param1, param2, param3);
            this.param4 = param4;

        }
    }

    public static final int TOTALPARAMS = PrefixParams.COUNT + SuffixParams.COUNT;

    protected double time;
    protected Prefix prefix;
    protected Suffix suffix;
    protected int sourceID;
    protected int destID;
    protected ArrayList<Integer> params;

    public CLEDescriptor() {
        this.params = new ArrayList<Integer>();
    }

    public String getSubEvent() {
        return this.prefix.asStr() + "_" + this.suffix.asStr();
    }

    /** getSubeventInt
     *
     * Returns an integer which is unique for every prefix, suffix combination
     */
    public int getSubEventInt() {
        return suffix.asInt() * 10 + prefix.asInt();
    }


}
