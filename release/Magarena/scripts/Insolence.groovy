[
    new MagicWhenBecomesTappedTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPermanent tapped) {
            final MagicPermanent enchantedCreature = permanent.getEnchantedPermanent();
            return (enchantedCreature.isCreature() && enchantedCreature==tapped) ?
                new MagicEvent(
                    permanent,
                    tapped.getController(),
                    this,
                    "SN deals 2 damage to PN."
                ) :
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game,final MagicEvent event) {
            final MagicDamage damage=new MagicDamage(event.getSource(),event.getPlayer(),2);
            game.doAction(new MagicDealDamageAction(damage));
        }
    }
]
