[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "Tap all nonwhite creatures."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final Collection<MagicPermanent> creatures = game.filterPermanents(MagicTargetFilterFactory.NONWHITE_CREATURE); 
            for (final MagicPermanent creature : creatures) {
                game.doAction(new MagicTapAction(creature,true));
            }
        }
    }
]