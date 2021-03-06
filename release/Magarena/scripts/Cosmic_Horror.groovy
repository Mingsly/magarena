[
    new MagicAtYourUpkeepTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPlayer upkeepPlayer) {
            return new MagicEvent(
                permanent,
                new MagicMayChoice(
                    "Pay {3}{B}{B}{B}?",
                    new MagicPayManaCostChoice(MagicManaCost.create("{3}{B}{B}{B}"))
                ),
                this,
                "PN may\$ pay {3}{B}{B}{B}\$. If PN doesn't, destroy SN. If SN is destroyed this way, it deals 7 damage to PN."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isNo()) {
                final MagicDestroyAction act = new MagicDestroyAction(event.getPermanent());
                game.doAction(act);
                if (act.isDestroyed()) {
                    final MagicDamage damage = new MagicDamage(event.getSource(),event.getPlayer(),7)
                    game.doAction(new MagicDealDamageAction(damage));
                }
            }
        }
    }
]
