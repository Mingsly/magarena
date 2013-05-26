[
    new MagicPermanentActivation(
        [MagicConditionFactory.ManaCost("{1}")],
        new MagicActivationHints(MagicTiming.Removal),
        "Destroy"
    ) {

        @Override
        public MagicEvent[] getCostEvent(final MagicPermanent source) {
            return [new MagicPayManaCostSacrificeEvent(source,"{1}")];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.NEG_TARGET_ARTIFACT_OR_ENCHANTMENT,
                new MagicDestroyTargetPicker(false),
                this,
                "Destroy target artifact or enchantment\$."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game,new MagicPermanentAction() {
                public void doAction(final MagicPermanent permanent) {
                    game.doAction(new MagicDestroyAction(permanent));
                }
            });
        }
    }
]
