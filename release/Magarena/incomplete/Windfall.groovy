[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "Each player discards his or her hand, then draws cards equal to the greatest number of cards a player discarded this way."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer cardPlayer = event.getPlayer();
            final int drawAmount = Math.max(cardPlayer.getHandSize(),cardPlayer.getOpponent().getHandSize());
            for (final MagicPlayer player : game.getPlayers()) {
                game.addEvent(new MagicDiscardEvent(event.getSource(),player,player.getHandSize()));
                game.addEvent(new MagicDrawEvent(event.getSource(),player,drawAmount));
            }
        }
    }
]
//Discarding and drawing should be simultaneous between players. Should end in a draw if both players would draw into empty libraries.