import java.util.HashSet;

public class Run {
    public static void main(String[] args) {
        // allow user to specify
        double simDur = 120.0;
        SimpleAPL apl = new SimpleAPL();
        apl.addItem(27180);
        apl.addItem(27138);
        apl.addItem(25364);


        HashSet<Integer> toLoad = new HashSet<Integer>();
        for (int ID : apl.getPrioList()) {
            toLoad.add(ID);
        }

        DBReader db = new DBReader();
        db.connect();

        Character character = new Character();

        GameState gameState = new GameState(character);

        SpellManager sm = new SpellManager(gameState);
        sm.importSpells(db, toLoad);

        db.close();

        CombatLog cl = new CombatLog();
        Summary summary = new Summary();

        System.out.println(character);

        while (gameState.time() < simDur) {
            //System.out.println(gameState.time());
            CombatLogEvent cle = sm.cast(apl.getNext(gameState, sm)).cle();
            cl.logEvent(cle);
            summary.processEvent(cle);
            gameState.waitTillCast();
            gameState.waitTillGCD();
        }

        System.out.println("\nDPS: " + summary.getDPS());

        System.out.println("Number of casts by spell: ");
        for (int ID : toLoad) {
            System.out.println("    " + SpellManager.getName(ID) + " : " + summary.getNumCasts(ID));
        }

    }
}
