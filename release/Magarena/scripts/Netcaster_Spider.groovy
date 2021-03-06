def EFFECT = MagicRuleEventAction.create("SN gets +2/+0 until end of turn.");

[
    new MagicWhenSelfBlocksTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent blocker) {
            final MagicPermanent blocked = permanent.getBlockedCreature();
            return blocked.isCreature() && blocked.hasAbility(MagicAbility.Flying) ?
                EFFECT.getEvent(permanent) :
                MagicEvent.NONE;
        }
    }
]
