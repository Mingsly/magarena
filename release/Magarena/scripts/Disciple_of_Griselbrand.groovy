[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Removal),
        "Gain Life"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicPayManaCostEvent(source,"{1}"),
                new MagicSacrificePermanentEvent(
                    source,
                    MagicTargetChoice.SACRIFICE_CREATURE
                )
            ];
        }
        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source,final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                payedCost.getTarget(),
                this,
                "PN gains life equal to RN's toughness."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final int sacrificed = event.getRefPermanent().getToughness();
            game.doAction(new MagicChangeLifeAction(event.getPlayer(),sacrificed));
        }
    }
]
