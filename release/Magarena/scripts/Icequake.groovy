[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.NEG_TARGET_LAND,
                MagicDestroyTargetPicker.Destroy,
                this,
                "Destroy target land\$. If that land was a snow land, SN deals 1 damage to that land's controller."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                game.doAction(new MagicDestroyAction(it));
                if (it.hasType(MagicType.Snow)) {
                    final MagicDamage damage=new MagicDamage(event.getSource(),it.getController(),1);
                    game.doAction(new MagicDealDamageAction(damage));
                }
            });
        }
    }
]
