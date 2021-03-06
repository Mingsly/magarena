[
    new MagicWhenOtherComesIntoPlayTrigger() {
        @Override
        public MagicEvent executeTrigger(final MagicGame game,final MagicPermanent permanent,final MagicPermanent otherPermanent) {
            return (otherPermanent.isFriend(permanent) &&
                    otherPermanent.hasSubType(MagicSubType.Ally)) ?
                new MagicEvent(
                    permanent,
                    new MagicMayChoice(
                        MagicTargetChoice.NEG_TARGET_ARTIFACT
                    ),
                    MagicDestroyTargetPicker.Destroy,
                    this,
                    "PN may\$ destroy target artifact\$. " +
                    "If that artifact is put into a graveyard this way, SN deals damage to that artifact's controller equal to the number of Allies you control."
                ) :
                MagicEvent.NONE;
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                event.processTargetPermanent(game, {
                    game.doAction(new MagicDestroyAction(it));
                    final MagicCard card = it.getCard();
                    final MagicPlayer player = event.getPlayer();
                    // only deal damage when the target is destroyed
                    if (card.isInGraveyard() 
                        ||
                        (card.isToken() && !card.getOwner().getPermanents().contains(it))) {
                        final int amount = player.getNrOfPermanents(MagicSubType.Ally);
                        final MagicDamage damage = new MagicDamage(
                            event.getPermanent(),
                            card.getOwner(),
                            amount
                        );
                        game.doAction(new MagicDealDamageAction(damage));
                    }
                });
            }
        }
    }
]
