[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "Put an X/X green Ooze creature tokens onto the battlefield. Where X is the greatest power among creatures you control."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            int x = 0;
            final Collection<MagicPermanent> targets = game.filterPermanents(
                        event.getPlayer(),
                        MagicTargetFilter.TARGET_CREATURE_YOU_CONTROL);
            for (final MagicPermanent creature : targets) {
                    x = Math.max(x,creature.getPower());
                }
            final MagicPlayer player=event.getPlayer();
            final MagicCardDefinition oozeDef = TokenCardDefinitions.get("green Ooze creature token");
            final MagicPlayTokenAction act = new MagicPlayTokenAction(player,oozeDef)
            final MagicStatic PT = new MagicStatic(MagicLayer.SetPT){
                    @Override
                    public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
                        pt.set(x,x);
                    }
                };
            game.doAction(act);
            game.doAction(new MagicAddStaticAction(act.getPermanent(),PT));
            
        }
    }
]