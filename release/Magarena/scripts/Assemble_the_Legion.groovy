[
    new MagicAtYourUpkeepTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPlayer upkeepPlayer) {
            return new MagicEvent(
                permanent,
                this,
                "PN puts a muster counter on SN. Then put a 1/1 red and white Soldier creature token with haste onto the battlefield for each muster counter on SN."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final MagicPermanent perm = event.getPermanent()
            game.doAction(new MagicChangeCountersAction(perm,MagicCounterType.Muster,1));
            game.doAction(new MagicPlayTokensAction(
                event.getPlayer(),
                TokenCardDefinitions.get("1/1 red and white Soldier creature token with haste"), 
                perm.getCounters(MagicCounterType.Muster)
            ));
        }
    }
]
