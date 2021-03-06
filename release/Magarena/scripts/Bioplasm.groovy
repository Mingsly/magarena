[
    new MagicWhenSelfAttacksTrigger() {
        @Override
         public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent attacker) {
            return new MagicEvent(
                permanent,
                this,
                "Exile the top card of PN's library. If it's a creature card, SN gets +X/+Y until end of turn, " + 
                "where X is the exiled creature card's power and Y is its toughness."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            for (final MagicCard card : event.getPlayer().getLibrary().getCardsFromTop(1)) {
                game.doAction(new MagicRemoveCardAction(
                    card,
                    MagicLocationType.OwnersLibrary
                ));
                game.doAction(new MagicMoveCardAction(
                    card,
                    MagicLocationType.OwnersLibrary,
                      MagicLocationType.Exile
                ));
                if (card.hasType(MagicType.Creature)) {
                    final int X = card.getPower();
                    final int Y = card.getToughness();
                    game.doAction(new MagicChangeTurnPTAction(event.getPermanent(), +X, +Y));
                }
            }
        }
    }
]
