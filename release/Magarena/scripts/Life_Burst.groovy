[
   new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.POS_TARGET_PLAYER,
                this,
                "Target player\$ gains 4 life, then 4 life for,"+
                "each card named Life Burst in all graveyards."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final int amount = game.filterCards(
                MagicTargetFilterFactory.cardName("Life Burst")
                .from(MagicTargetType.Graveyard)
                .from(MagicTargetType.OpponentsGraveyard)
            ).size()*4;
            event.processTargetPlayer(game, {
                game.doAction(new MagicChangeLifeAction(it,4));
                game.logAppendMessage(event.getPlayer()," (X="+amount+")");
                game.doAction(new MagicChangeLifeAction(it,4*amount));
            });
        }
    }
]
