[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPayedCost payedCost) {      
            return new MagicEvent(
                permanent,
                MagicTargetChoice.NEG_TARGET_PLAYER,
                this,
                "When SN enters the battlefield, it deals damage to target player\$ equal to "+
                "the number of nonbasic lands that player controls."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPlayer(game, {
                final MagicPlayer target ->
                final int amount = target.getNrOfPermanents(MagicTargetFilterFactory.NONBASIC_LAND_YOU_CONTROL)
                final MagicDamage damage = new MagicDamage(event.getSource(),target,amount);
                game.doAction(new MagicDealDamageAction(damage));
                game.logAppendMessage(event.getPlayer(),"("+amount+")");
            });
        }
    }
]