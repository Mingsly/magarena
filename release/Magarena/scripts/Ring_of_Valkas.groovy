[
    new MagicAtYourUpkeepTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPlayer upkeepPlayer) {
            final MagicPermanent equipped = permanent.getEquippedCreature();
            return equipped.isValid() && equipped.hasColor(MagicColor.Red) ?
                new MagicEvent(
                    permanent,
                    this,
                    "PN puts a +1/+1 counter on creature equipped by SN if it's red."
                ):
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPermanent permanent=event.getPermanent();
            final MagicPermanent equipped=permanent.getEquippedCreature();
            if (equipped.isValid() && equipped.hasColor(MagicColor.Red)) {
                game.doAction(new MagicChangeCountersAction(equipped,MagicCounterType.PlusOne,1));
            }
        }
    }
]
