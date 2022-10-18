import java.sql.ResultSet;
import java.util.HashMap;

public class Spell {

    private enum SpellType {
        melee(0), ranged(1), spell(2);

        private int val;

        SpellType(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private enum School {
        physical(0), holy(1), frost(2);

        private int val;

        School(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }

    }
    private enum Req {
        enemyhpmax(0), enemyhpmin(1), hasbuff(2);

        private int val;

        Req(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private int ID;
    private Modify base_dmg_min;
    private Modify base_dmg_max;
    private Modify base_scaling_SP;
    private Modify base_scaling_AP;
    private Modify cast_time;
    private boolean cast_haste;
    private Modify gcd;
    private boolean gcd_haste;
    private Modify cooldown;

    private SpellType spellType;
    private School school;

    private int lastHash;
    private double lastCast;
    private double cdOver;

    private HashMap<Req, Integer> reqs;


    public Spell(int ID, ResultSet record, DBReader reader) {
        this.ID = ID;

        try {
            // read basic spell fields
            this.base_dmg_min = new Modify(record.getInt("base_dmg_min"));
            this.base_dmg_max = new Modify(record.getInt("base_dmg_max"));
            this.base_scaling_SP = new Modify(record.getDouble("base_scaling_SP") / 100.0); // convert percentage value to decimal value
            this.base_scaling_AP = new Modify(record.getDouble("base_scaling_AP") / 100.0);
            this.cast_time = new Modify(record.getDouble("cast_time"));
            this.cast_haste = record.getBoolean("cast_haste");
            this.gcd = new Modify(record.getDouble("gcd"));
            this.gcd_haste = record.getBoolean("gcd_haste");
            this.cooldown = new Modify(record.getDouble("cooldown"));

            this.spellType = SpellType.valueOf(record.getString("spell_type"));
            this.school = School.valueOf(record.getString("school"));

            // read spell requirements
            this.reqs = new HashMap<Req, Integer>();

            ResultSet reqSet = reader.doQuery("SELECT conditions FROM spells.spellreq WHERE spell_id=" +
                    String.valueOf(ID) + ";");


            if(reqSet.next()) {
                String reqStr = reqSet.getString("conditions");
                System.out.println(reqStr);
                String[] reqSplit = reqStr.split("\\|\\|");
                System.out.println(reqSplit[0]);
                for (String r : reqSplit) {
                    if (r.contains("enemyhp")) {
                        char comp = r.charAt(7);
                        int val = Integer.parseInt(r.substring(8));

                        switch (comp) {
                            case '<':
                                reqs.put(Req.enemyhpmax, val);
                                break;
                            case '>':
                                reqs.put(Req.enemyhpmin, val);
                                break;
                        }

                        continue;
                    }

                    if (r.contains("hasbuff")) {
                        int buffid = Integer.parseInt(r.substring(8).replace(")", ""));
                        reqs.put(Req.hasbuff, buffid);
                        continue;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read fields for spell ID: " + String.valueOf(ID));
        }

        this.lastHash = -1;



    }

    private int rollBaseVal(GameState gameState) {
        double random = gameState.rand();
        int rolled = base_dmg_min.getInt() + (int) (random*(base_dmg_max.getDouble() - base_dmg_min.getDouble()));
        int apBonus = (int) (base_scaling_AP.getDouble() * (double) gameState.getPlayer().getAPBonus());
        int rapBonus = (int) (base_scaling_AP.getDouble() * (double) gameState.getPlayer().getRAPBonus());
        int spellBonus = (int) (base_scaling_SP.getDouble() * (double) gameState.getPlayer().getSpellBonus(spellType.asInt(), school.asInt()));
        int afterBonus = rolled + apBonus + rapBonus + spellBonus;
        return afterBonus;
    }

    private CombatLogEvent rollVal(GameState gameState) {
        boolean crit = gameState.rand() < gameState.getPlayer().getCritP(spellType.asInt(), school.asInt());
        int amt = rollBaseVal(gameState);
        if (crit) { amt = (int) ((double) amt * 1.5); }

        CombatLogEvent cle = new CombatLogEvent(gameState.time(), ID, amt, crit);

        return cle;
    }

    private void update(GameState gameState) {

    }

    public boolean canCast(GameState gameState) {
        boolean cdCheck = gameState.time() >= cdOver;//(gameState.time() - lastCast) >= cooldown.getDouble();
        boolean enemyhpcheck = true;

        if (reqs.containsKey(Req.enemyhpmax)) {
            enemyhpcheck = gameState.getTarget().getHPP() <= reqs.get(Req.enemyhpmax);
        } else if (reqs.containsKey(Req.enemyhpmin)) {
            enemyhpcheck = gameState.getTarget().getHPP() >= reqs.get(Req.enemyhpmax);
        }

        boolean hasbuffcheck = true;

        if (reqs.containsKey(Req.hasbuff)) {
            hasbuffcheck = gameState.getPlayer().hasBuff(reqs.get(Req.hasbuff));
        }

        return cdCheck && (enemyhpcheck || hasbuffcheck);
    }

    public Event cast(GameState gameState) {
        int h;
        if ((h = gameState.getSpHash()) != lastHash) {
            this.lastHash = h;
            update(gameState);
        }

        this.lastCast = gameState.time();
        this.cdOver = gameState.time() + cooldown.getDouble();

        double dt = cast_time.getDouble();
        double gcdTime = gcd.getDouble();
        CombatLogEvent cle = rollVal(gameState);

        gameState.invokeCast(dt);
        gameState.invokeGCD(gcdTime);

        return new Event(dt, gcdTime, cle);
    }

}
