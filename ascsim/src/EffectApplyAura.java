/** EffectApplyAura.java
 *
 * When this effect is executed, applies an aura to an entity
 *
 *
 */

public class EffectApplyAura extends Effect {

    private enum EffectSubType {
        to_caster(0), to_target(1);

        private int val;

        EffectSubType(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private EffectSubType subType;
    private int auraToApply;

    public EffectApplyAura(boolean targetSelf, int auraToApply) {
        super("apply_aura");

        if (targetSelf) {
            subType = EffectSubType.to_caster;
        } else {
            subType = EffectSubType.to_target;
        }

        this.auraToApply = auraToApply;
    }

    /** execute
     *
     * Applies the aura to the character or to the character's target
     *
     * @param gameState
     */
    public void execute(GameState gameState) {
        //gameState.getPlayer().applyAuraByID(auraToApply);
        CombatLogEvent.Suffix applyType = CombatLogEvent.Suffix.AURA_APPLIED;

        int target = this.subType == EffectSubType.to_caster ? 0 : gameState.curTargetID(); // target ID - TODO need to allow specification from outside, what if multi target?
        int auraSchool = AuraManager.getAuraByID(auraToApply).getSchool().asInt();

        if (target == 0 && gameState.getPlayer().hasAura(auraToApply)) {
            applyType = CombatLogEvent.Suffix.AURA_REFRESH;
        } else if (target != 0 && gameState.getTarget(target).hasAura(auraToApply)) {
            applyType = CombatLogEvent.Suffix.AURA_REFRESH;
        }

        gameState.applyAuraByID(auraToApply, target);

        CombatLogEvent.PrefixParams pp1 = new CombatLogEvent.PrefixParams(CombatLogEvent.Prefix.SPELL, auraToApply, auraSchool);
        CombatLogEvent.SuffixParams sp1 = new CombatLogEvent.SuffixParams(applyType, -1, -1, -1);
        CombatLogEvent cle_spell_aura_applied = new CombatLogEvent(gameState.time(), pp1, sp1, 0, target);
        cle_spell_aura_applied.setSource(0);
        cle_spell_aura_applied.setDest(target);

        gameState.logEvent(cle_spell_aura_applied);
    }
}
