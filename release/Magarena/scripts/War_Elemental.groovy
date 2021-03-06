def EFFECT = MagicRuleEventAction.create("Sacrifice SN.");

[
    new MagicWhenComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPayedCost payedCost) {
            return permanent.getOpponent().hasState(MagicPlayerState.WasDealtDamage) == false ?
                EFFECT.getEvent(permanent):
                MagicEvent.NONE;
        }
    },
    new MagicWhenDamageIsDealtTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicDamage damage) {
            return permanent.isOpponent(damage.getTarget()) ?
                new MagicEvent(
                    permanent,
                    damage.getDealtAmount(),
                    this,
                    "PN puts RN +1/+1 counters on SN."
                ):
                MagicEvent.NONE;
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            game.doAction(new MagicChangeCountersAction(event.getPermanent(),MagicCounterType.PlusOne,event.getRefInt()));
        }
    }
]
