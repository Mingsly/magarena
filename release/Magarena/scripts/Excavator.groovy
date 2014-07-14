[
    new MagicPermanentActivation(
        new MagicActivationHints(MagicTiming.Attack),
        "Landwalk"
    ) {

        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicPermanent source) {
            return [
                new MagicTapEvent(source), 
                new MagicSacrificePermanentEvent(source, new MagicTargetChoice("a basic land to sacrifice"))
            ];
        }

        @Override
        public MagicEvent getPermanentEvent(final MagicPermanent source, final MagicPayedCost payedCost) {
            return new MagicEvent(
                source,
                MagicTargetChoice.POS_TARGET_CREATURE,
                MagicUnblockableTargetPicker.create(),
                payedCost.getTarget(),
                this,
                "Target creature\$ gains landwalk of each of the land types of the sacrificed land until end of turn."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTarget(game, {
                final MagicPermanent target ->
                final MagicPermanent sacLand = event.getRefPermanent();
                for (final MagicSubType subType : MagicSubType.ALL_BASIC_LANDS) {
                    if (sacLand.hasSubType(subType)) {
                        game.doAction(new MagicGainAbilityAction(target,subType.getLandwalkAbility()));
                    }
                }
            });
        }
    }
]