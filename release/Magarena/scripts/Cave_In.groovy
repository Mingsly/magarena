[
    new MagicCardActivation(
        [
            MagicConditionFactory.OtherCardInHand(MagicColor.Red, 1), 
            MagicCondition.CARD_CONDITION
        ],
        new MagicActivationHints(MagicTiming.Main, true),
        "Alt"
    ) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicCard source) {
            final MagicTargetChoice targetChoice = new MagicTargetChoice(
                new MagicOtherCardTargetFilter(
                    MagicTargetFilterFactory.RED_CARD_FROM_HAND,
                    source
                ),
                MagicTargetHint.None,
                "a red card from your hand"
            );
            return [
                new MagicExileCardEvent(source, targetChoice)
            ];
        }
    }
]
