def COLORED_PERMANENT = new MagicPermanentFilterImpl() {
    public boolean accept(final MagicGame game,final MagicPlayer player,final MagicPermanent target) {
        return MagicColor.isColorless(target) == false;
    }
};

[
    new MagicSpellCardEvent() {
        @Override
        public MagicEvent getEvent(final MagicCardOnStack cardOnStack, final MagicPayedCost payedCost) {
            return new MagicEvent(
                cardOnStack,
                this,
                "Each player sacrifices all colored permanents he or she controls."
            );
        }

        @Override
        public void executeEvent(final MagicGame game, final MagicEvent event) {
            for (final MagicPermanent target : game.filterPermanents(COLORED_PERMANENT)) {
                game.doAction(new MagicSacrificeAction(target));
            }    
        }
    }
]
