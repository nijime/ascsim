import java.util.concurrent.TimeUnit;

public class Run {
    /* TODO delete?
    public static HashSet<Pair<Integer, Integer>> talentStrToSet(String s) {
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
*/

    public static void main(String[] args) {

        Control.run(Control.DEFAULT_APL, Control.DEFAULT_CHAR, 120.0);
        System.out.println("Sim took " + Model.getExecutionTime() +  " ms");
        /// 11/11 - 13ms + 2ms/minute

        System.out.println("\nDPS: " + Model.getDPS());
        System.out.println("Spell statistics: ");
        System.out.println("    " + Utils.rightPad("Name", 20) + " : " +
                Utils.rightPad("Damage" + "", 6) + "| " +
                Utils.rightPad("dmg %" + "", 6) + "| " +
                "casts" + "\n    ---------------------------------------------");

        for (Model.SpellDisplayRow row : Model.getSpellDisplay()) {
            System.out.println(row);
        }

        System.out.println("Aura statistics: ");
        System.out.println("    " + Utils.rightPad("Name", 20) + " : " +
                Utils.rightPad("Uptime" + "", 6) + "| " +
                Utils.rightPad("  % " + "", 6) + "| " +
                "count" + "\n    ---------------------------------------------");
        for (Model.AuraDisplayRow row : Model.getAuraDisplay()) {
            System.out.println(row);
        }

        /* TODO delete?
        // allow user to specify
        double simDur = 120.0;
        SimpleAPL apl = new SimpleAPL();
        apl.addItem(27180);
        apl.addItem(27138);
        apl.addItem(25368); //25364
        apl.addItem(25364);

        HashSet<Integer> toLoad = new HashSet<Integer>();
        for (int ID : apl.getPrioList()) {
            toLoad.add(ID);
        }

        DBReader db = new DBReader();
        db.connect();

        //SpellEffects spellEffects = new SpellEffects();

        Character.CharBaseStats cBase = new Character.CharBaseStats(100, 100, 100, 100, 55, 287, 42, 12);
        Character character = new Character(cBase);

        GameState gameState = new GameState(character);

        Character.importTalents(db, talentStrToSet("35397:3,1001:99"), gameState);

        SpellManager sm = new SpellManager(gameState);
        sm.importSpells(db, toLoad);

        db.close();

        System.out.println(character);

        while (gameState.time() < simDur) {
            sm.cast(apl.getNext(gameState, sm));
            gameState.waitTillCast();
            gameState.waitTillGCD();
        }

*/
        /* print summary */

        /*
        Summary summary = gameState.getSummary();

        System.out.println("\nDPS: " + summary.getDPS());

        System.out.println("Damage by spell: ");
        for (int ID : toLoad) {
            System.out.println("    " + Utils.rightPad(SpellManager.getName(ID), 20) + " : " + summary.getSpellDamage(ID) +
                    ", " + summary.getSpellDamagePercent(ID) + "%");
        }

        System.out.println("\nNumber of casts by spell: ");
        for (int ID : toLoad) {
            System.out.println("    " + Utils.rightPad(SpellManager.getName(ID), 20) + " : " + summary.getNumCasts(ID));
        }


        System.out.println("\nUptime by aura: ");
        for (int ID : summary.getAuraSet()) {
            System.out.println("    " + Utils.rightPad(AuraManager.getName(ID), 20) + " : " +
                    Utils.rightPad(summary.getUptime(ID) + "s", 6) + "| " +
                    Utils.rightPad(summary.getUptimePercent(ID) + "% ", 6) + "| " +
                    summary.getAuraApplications(ID));
        }
*/
    }
}
