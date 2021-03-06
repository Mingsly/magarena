[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_PLAYER,
                this,
                "Target player\$ draws three cards, loses 3 life, and gets three poison counters."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                game.doAction(new MagicDrawAction(it, 3));
                game.doAction(new MagicChangeLifeAction(it, -3));
                game.doAction(new MagicChangePoisonAction(it, 3));
            });
        }
    }
]
