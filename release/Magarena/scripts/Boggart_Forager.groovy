[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Removal),
        "Shuffle"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicSacrificeEvent(source), new MagicPayManaCostEvent(source, "{R}")
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.TARGET_PLAYER,
                this,
                "Target player\$ shuffles his or her library."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                game.doAction(new MagicShuffleLibraryAction(it));
            });
        }
    }
]
