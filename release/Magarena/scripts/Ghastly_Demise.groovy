[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_NONBLACK_CREATURE,
                this,
                "Destroy target nonblack creature\$ if its toughness is less than or equal to the number of cards in your graveyard."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                final MagicPermanent creature ->
                if (creature.getToughness() <= event.getPlayer().getGraveyard().size()) {
                    game.doAction(new MagicDestroyAction(creature));
                }
            });
        }
    }
]