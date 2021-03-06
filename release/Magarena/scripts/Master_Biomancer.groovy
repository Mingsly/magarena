def MUTANT = new MagicStatic(MagicLayer.Type) {
    @Override
    public void modSubTypeFlags(final MagicPermanent permanent,final Set<MagicSubType> flags) {
        flags.add(MagicSubType.Mutant);
    }
};
[
    new MagicWhenOtherComesIntoPlayTrigger(MagicTrigger.REPLACEMENT) {
        @Override
        public MagicEvent executeTrigger(final MagicGame game, final MagicPermanent permanent, final MagicPermanent otherPermanent) {
            if (otherPermanent != permanent &&
                otherPermanent.isCreature() &&
                otherPermanent.isFriend(permanent)) {
                    final int amount = permanent.getPower();
                    game.doAction(new MagicChangeCountersAction(otherPermanent,MagicCounterType.PlusOne,amount));
                    game.doAction(new MagicAddStaticAction(otherPermanent,MUTANT));
                    game.logAppendMessage(
                        permanent.getController(),
                        ""+otherPermanent.getName()+" enters the battlefield with an additional "+amount+
                        " +1/+1 counters on it, and as a Mutant in addition to its other types."
                    );
                }
            return MagicEvent.NONE;
        }
    }    
]
