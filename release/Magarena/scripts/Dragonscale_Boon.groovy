[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.POS_TARGET_CREATURE,
                MagicTapTargetPicker.Untap,
                this,
                "PN puts two +1/+1 counters on target creature\$ and untaps it."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                game.doAction(new MagicChangeCountersAction(it,MagicCounterType.PlusOne,2));
                game.doAction(new MagicUntapAction(it));
            });
        }
    }
]
