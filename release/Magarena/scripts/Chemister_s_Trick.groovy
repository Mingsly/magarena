[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                MagicTargetChoice.TARGET_CREATURE_YOU_DONT_CONTROL,
                MagicTapTargetPicker.Tap,
                this,
                "Target creature\$ you don't control gets -2/-0 until end of turn and attacks this turn if able."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            event.processTargetPermanent(game, {
                game.doAction(new MagicChangeTurnPTAction(it, -2, 0));
                game.doAction(new MagicGainAbilityAction(it, MagicAbility.AttacksEachTurnIfAble));
            });
        }
    },
    new MagicOverloadActivation(MagicTiming.MustAttack) {
        @Override
        public Iterable<MagicEvent> getCostEvent(final MagicCard source) {
            return [
                new MagicPayManaCostEvent(source,"{3}{U}{R}")
            ];
        }
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack,final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "Each creature you don't control gets -2/-0 until end of turn and attacks this turn if able."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            final Collection<MagicPermanent> targets=
                game.filterPermanents(event.getPlayer(),MagicTargetFilterFactory.CREATURE_YOUR_OPPONENT_CONTROLS);
            for (final MagicPermanent creature : targets) {
                game.doAction(new MagicChangeTurnPTAction(creature, -2, 0));
                game.doAction(new MagicGainAbilityAction(creature, MagicAbility.AttacksEachTurnIfAble));
            }
        }
    }
]
