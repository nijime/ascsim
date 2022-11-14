import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Control {

    public static final String DEFAULT_APL = "27180, 27138, 25368, 25364";
    public static final String DEFAULT_CHAR = "strength=100;agility=100;intellect=100;spirit=100;haste=210;crit=287;hit=10;expertise=12;";

    public static final String INVALID_APL_ERROR = "Invalid APL string";
    public static final String INVALID_CHAR_ERROR = "Invalid character import string";


    public static int run(String aplStr, String charStr, double duration) {
        SimpleAPL apl = new SimpleAPL();
        apl.fromStr(DEFAULT_APL);

        ArrayList<Integer> IDList = apl.getSpellList();
        if (IDList == null) {
            return 1; // INVALID_APL_ERROR
        }

        Character.CharBaseStats cBase = strToStats(charStr);
        HashSet<Pair<Integer, Integer>> talentSet = strToTalents(charStr);
        if (IDList == null || talentSet == null) {
            return 2; // INVALID_CHAR_ERROR
        }






        HashSet<Integer> toLoad = new HashSet<Integer>();
        for (int ID : apl.getSpellList()) {
            toLoad.add(ID);
        }

        DBReader db = new DBReader();
        db.connect();

        Character character = new Character(cBase);

        GameState gameState = new GameState(character);

        Character.importTalents(db, talentSet, gameState);

        SpellManager sm = new SpellManager(gameState);
        sm.importSpells(db, toLoad);

        db.close();

        System.out.println(character);


        gameState.getSummary().startTime();
        while (gameState.time() < duration) {
            sm.cast(apl.getNext(gameState, sm));
            gameState.waitTillCast();
            gameState.waitTillGCD();
        }
        gameState.getSummary().endTime();


        Model.setSummary(gameState.getSummary());
        return 0; // successful run
    }


    /** strToStats
     *
     * Converts character import string to CharBaseStats object, returns null if stats string is deformed
     *
     * @param statString
     * @return
     */
    private static Character.CharBaseStats strToStats(String statString) {
        String noSpace = statString.replace(" ", "");
        String[] splt = noSpace.split(";");

        HashMap<String, Integer> statMap = new HashMap<String, Integer>();

        for (int i = 0; i < splt.length; i++) {
            String thisStr = splt[i];
            String[] statSplit = thisStr.split("=");
            String statName = statSplit[0];
            int statVal;
            try {
                statVal = Integer.parseInt(statSplit[1]);
            } catch (Exception e) {
                // invalid string
                return null;
            }

            statMap.put(statName, statVal);
        }

        Character.CharBaseStats toReturn;

        try {
            int str = statMap.get("strength");
            int agi = statMap.get("agility");
            int intellect = statMap.get("intellect");
            int spirit = statMap.get("spirit");
            int haste = statMap.get("haste");
            int crit = statMap.get("crit");
            int hit = statMap.get("hit");
            int expertise = statMap.get("expertise");

            toReturn = new Character.CharBaseStats(str, agi, intellect, spirit, haste, crit, hit, expertise);
        } catch (Exception e) {
            // stat import string is not comprehensive
            return null;
        }

        return toReturn;

    }

    private static HashSet<Pair<Integer, Integer>> strToTalents(String statString) {
        // placeholder TODO expand

        return talentStrToSet("35397:3,1001:99");
    }

    /** talentStrToSet
     *
     * Converts talent string to set of (talent ID, rank)
     *
     * @param s
     * @return
     */
    private static HashSet<Pair<Integer, Integer>> talentStrToSet(String s) {
        HashSet<Pair<Integer, Integer>> toLoad = new HashSet<Pair<Integer, Integer>>();

        String[] talentStrs = s.split(",");

        for (String talentStr : talentStrs) {
            String[] talentSplit = talentStr.split(":");
            int ID = Integer.parseInt(talentSplit[0]);
            int rank = Integer.parseInt(talentSplit[1]);

            toLoad.add(new Pair<Integer, Integer>(ID, rank));
        }

        return toLoad;
    }

}
