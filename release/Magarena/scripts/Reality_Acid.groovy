[
    new MagicWhenLeavesPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicRemoveFromPlayAction act) {
            final MagicPermanent enchantedPermanent = permanent.getEnchantedPermanent();
            return (act.isPermanent(permanent) && enchantedPermanent.isValid()) ?
                new MagicEvent(
                    permanent,
                    enchantedPermanent,
                    this,
                    "RN's controller sacrifices it."
                ) :
            MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicSacrificeAction(event.getRefPermanent()));
        }
    }
]
