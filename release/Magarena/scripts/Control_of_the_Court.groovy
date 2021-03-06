[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack, final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "PN draws four cards then discards three cards at random."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPlayer player = event.getPlayer()
                game.doAction(new MagicDrawAction(player, 4));
                game.addEvent(MagicDiscardEvent.Random(event.getSource(), player, 3));
        }
    }
]
