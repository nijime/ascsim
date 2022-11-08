/** AuraManager.java
 *
 * Stores all required aura data that have been loaded from the database
 *
 */

import java.sql.ResultSet;
import java.util.HashMap;


public class AuraManager {

    private static HashMap<Integer, String> nameMap = new HashMap<Integer, String>(); // for relating IDs to names

    public static String getName(int ID) {
        return nameMap.get(ID);
    }

    private static HashMap<Integer, Aura> auraMap = new HashMap<Integer, Aura>(); // relate ID to Aura

    public AuraManager() {

    }

    /** getAuraByID(int ID)
     *
     * Returns the Aura with given ID
     *
     * @param ID, ID of the aura to query
     * @return Aura, the aura base with specified ID
     */
    public static Aura getAuraByID(int ID) {
        if (!auraMap.containsKey(ID)) {
            return null;
        }

        return auraMap.get(ID);
    }

    /** loadAuraByID(DBReader reader, int ID)
     *
     * Attempts to retrieve and load the aura with given ID from the DB opened in reader
     *
     * @param reader
     * @param ID
     */
    public static void loadAuraByID(DBReader reader, int ID) {
        if (auraMap.containsKey(ID)) {
            // dont try to load auras that are already loaded
           return;
        }

        ResultSet result = reader.doQuery("SELECT * FROM auras.aura WHERE spell_id = '" + ID + "';");

        try {
            if (!result.next()) {
                System.out.println("[AuraManager] No aura found with ID: " + ID);
                System.exit(-1);
            }

            String auraName = result.getString("aura_name");
            boolean harmful = result.getBoolean("is_harmful");
            double duration = result.getDouble("base_duration");

            // check if this aura is also a periodic type
            ResultSet pResult = reader.doQuery("SELECT * FROM auras.auraperiodic WHERE parent_spell_id = '" + ID + "';");

            if (pResult.next()) {
                int numTicks = pResult.getInt("num_ticks");
                int totalDamage = pResult.getInt("total_damage");
                double scalingSP = pResult.getDouble("dot_scaling_SP");
                double scalingAP = pResult.getDouble("dot_scaling_AP");
                String dmgSchool = pResult.getString("dmg_school");

                AuraPeriodic thisAura = new AuraPeriodic(ID, harmful, duration, Utils.School.valueOf(dmgSchool), numTicks, totalDamage, scalingSP, scalingAP);
                auraMap.put(ID, thisAura);
                nameMap.put(ID, auraName);

                System.out.println("[AuraManager] Imported periodic aura with ID: " + ID);
                return;
            }


            Aura thisAura = new Aura(ID, harmful, duration);
            auraMap.put(ID, thisAura);
            nameMap.put(ID, auraName);

            System.out.println("[AuraManager] Imported aura with ID: " + ID);
        } catch (Exception e) {
            System.out.println("[AuraManager] Failed to load aura with ID: " + ID);
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
