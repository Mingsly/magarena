[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Pump),
        "+Counters"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source),
                new MagicSacrificeEvent(source)
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.POS_TARGET_CREATURE,
                this,
                "Put X +0/+1 counters on target creature\$, where X is that creature's converted mana cost."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                game.doAction(new MagicChangeCountersAction(
                    it,
                    MagicCounterType.PlusZeroPlusOne,
                    it.getConvertedCost()
                ));
            });
        }
    }
]
