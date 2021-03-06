def choice = new MagicTargetChoice("an Island you control");

[
     new MagicCardActivation(
        [MagicCondition.CARD_CONDITION],
        new MagicActivationHints(MagicTiming.Counter, true),
        "Alt"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicCard source) {
            return [ 
                new MagicRepeatedPermanentsEvent(source, choice, 3, MagicChainEventFactory.Bounce)
            ];
        }
    }
]
