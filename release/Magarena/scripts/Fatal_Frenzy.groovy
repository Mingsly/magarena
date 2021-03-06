[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack, final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_CREATURE_YOU_CONTROL,
                MagicPumpTargetPicker.create(),
                this,
                "Target creature\$ gains trample and gets +X/+0 until end of turn, where X is its power. " +
                "Sacrifice it at the beginning of the next end step."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                game.doAction(new MagicGainAbilityAction(it, MagicAbility.Trample));
                game.doAction(new MagicChangeTurnPTAction(
                    it,
                    it.getPower(),
                    0
                ));
                game.doAction(new MagicAddTriggerAction(it, MagicAtEndOfTurnTrigger.Sacrifice));
            });
        }
    }
]
