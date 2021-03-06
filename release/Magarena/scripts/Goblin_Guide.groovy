[
     new MagicWhenSelfAttacksTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent attacker) {
            return new MagicEvent(
                permanent,
                game.getDefendingPlayer(),
                this,
                "PN reveals the top card of his or her library. If it's a land card, PN puts it into his or her hand."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            for (final MagicCard card : event.getPlayer().getLibrary().getCardsFromTop(1)) {
                if (card.hasType(MagicType.Land)) {
                    game.doAction(new MagicRemoveCardAction(
                        card,
                        MagicLocationType.OwnersLibrary
                    ));
                    game.doAction(new MagicMoveCardAction(
                        card,
                        MagicLocationType.OwnersLibrary,
                        MagicLocationType.OwnersHand
                    ));
                } 
            }
        }
    }
]
