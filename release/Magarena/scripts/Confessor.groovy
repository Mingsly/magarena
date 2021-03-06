[
    new MagicWhenDiscardedTrigger() {
        @Override
        public MagicEvent getEvent(final MagicPermanent permanent,final MagicCard card) {
            return new MagicEvent(
                permanent,
                new MagicSimpleMayChoice(
                    MagicSimpleMayChoice.GAIN_LIFE,
                    1,
                    MagicSimpleMayChoice.DEFAULT_YES
                ),
                this,
                "PN may\$ gain 1 life."
            );
        }
        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            if (event.isYes()) {
                game.doAction(new MagicChangeLifeAction(event.getPlayer(),1));
            }
        }
    }
]
