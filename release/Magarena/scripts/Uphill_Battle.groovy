[
    new MagicWhenOtherComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent otherPermanent) {
            if (otherPermanent.isEnemy(permanent) && otherPermanent.isCreature()) {
                game.doAction(MagicTapAction.Enters(otherPermanent));
            }
            return MagicEvent.NONE;
        }
    }
]
