[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent, final MagicPayedCost payedCost) {
            return new MagicEvent(
                permanent,
                this,
                "PN exiles all cards from his or her library."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            for (final MagicCard card : new MagicCardList(event.getPermanent().getController().getLibrary())) {
                game.doAction(new MagicRemoveCardAction(card, MagicLocationType.OwnersLibrary));
                game.doAction(new MagicMoveCardAction(card, MagicLocationType.OwnersLibrary, MagicLocationType.Exile));
            }
        }
    }
]
