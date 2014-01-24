[
        new MagicStatic(MagicLayer.Player) {
        @Override
        public void modPlayer(final MagicPermanent source, final MagicPlayer player) {
            player.noMaxHandSize();
        }
    },
    
    new MagicAtUpkeepTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPlayer upkeepPlayer) {
            return permanent.isController(upkeepPlayer) ?
                new MagicEvent(
                    permanent,
                    upkeepPlayer,
                    this,
                    "PN gains X life where X is the number of cards in his or her hand."
                ) :
                MagicEvent.NONE;
        }
 
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer();
            final int amount = player.getHandSize();
            if (amount > 0) {
                game.doAction(new MagicChangeLifeAction(player,amount));
            }
        }
    }
]