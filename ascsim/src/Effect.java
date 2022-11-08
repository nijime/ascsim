/** Effect.java
 *
 * Abstract class to base effect classes on
 *
 * Effects are actions which might be taken in certain circumstances, ex. applying an aura
 *
 * Tracks effect type
 */


public abstract class Effect {

    private enum EffectType {
        apply_aura(0), modify_damage(1), unleash_seal(2), remove_aura(3);

        private int val;

        EffectType(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private EffectType name;


    public Effect(String effectName) {
        try {
            this.name = EffectType.valueOf(effectName);
        } catch (Exception e) {
            System.out.println("[Effect] No effect with specified name");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void execute(GameState gameState) {

    }
}
