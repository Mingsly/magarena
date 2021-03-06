[
    new MagicWhenSelfTargetedTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicItemOnStack target) {
            return new MagicEvent(
                permanent,
                target.getController(),
                this,
                "SN deals 3 damage to PN."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicDamage damage = new MagicDamage(event.getPermanent(), event.getPlayer(), 3);
            game.doAction(new MagicDealDamageAction(damage));
        }
    }
]
