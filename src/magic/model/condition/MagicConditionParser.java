package magic.model.condition;

import magic.model.ARG;
import magic.model.target.MagicTargetFilterFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum MagicConditionParser {
            
    YouControl("you control a(n)? " + ARG.WORDRUN) {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicConditionFactory.YouControl(
                MagicTargetFilterFactory.singlePermanent(ARG.wordrun(arg))
            );
        }
    },
    OpponentControl("an opponent controls a(n)? " + ARG.WORDRUN) {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicConditionFactory.OpponentControl(
                MagicTargetFilterFactory.singlePermanent(ARG.wordrun(arg))
            );
        }
    },
    DefenderControl("defending player controls a(n)? " + ARG.WORDRUN) {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicConditionFactory.OpponentControl(
                MagicTargetFilterFactory.singlePermanent(ARG.wordrun(arg))
            );
        }
    },
    YouControlAnother("you control another " + ARG.WORDRUN) {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicConditionFactory.YouControlAnother(
                MagicTargetFilterFactory.singlePermanent(ARG.wordrun(arg))
            );
        }
    },
    Threshold("seven or more cards are in your graveyard") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.THRESHOLD_CONDITION;
        }
    },
    HandSevenOrMore("you have seven or more cards in hand") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicConditionFactory.HandAtLeast(7);
        }
    },
    Metalcraft("you control three or more artifacts") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.METALCRAFT_CONDITION;
        }
    },
    NoUntappedLands("you control no untapped lands") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.NO_UNTAPPED_LANDS_CONDITION;
        }
    },
    Hellbent("you have no cards in hand") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.HELLBENT;
        }
    },
    IsEquipped("SN is equipped") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.IS_EQUIPPED;
        }
    },
    IsEnchanted("(SN is|it's) enchanted") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.IS_ENCHANTED;
        }
    },
    IsUntapped("(SN is|it's) untapped") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.UNTAPPED_CONDITION;
        }
    },
    IsMonstrous("SN is monstrous") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.IS_MONSTROUS_CONDITION;
        }
    },
    IsAttacking("it's attacking") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.IS_ATTACKING_CONDITION;
        }
    },
    AttackingAlone("it's attacking alone") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.IS_ATTACKING_ALONE_CONDITION;
        }
    },
    NoCardsInGraveyard("there are no cards in your graveyard") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.EMPTY_GRAVEYARD_CONDITION;
        }
    },
    LibraryWithLEQ20Cards("a library has twenty or fewer cards in it") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.LIBRARY_HAS_20_OR_LESS_CARDS_CONDITION;
        }
    },
    OpponentGraveyardWithGEQ10Cards("an opponent has ten or more cards in his or her graveyard") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.OPP_GRAVEYARD_WITH_10_OR_MORE_CARDS_CONDTITION;
        }
    },
    OpponentNotControlWhiteOrBlueCreature("no opponent controls a white or blue creature") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.OPP_NOT_CONTROL_WHITE_OR_BLUE_CREATURE_CONDITION;
        }
    },
    MostCardsInHand("you have more cards in hand than each opponent") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.MOST_CARDS_IN_HAND_CONDITION;
        }
    },
    NoShellCounters("SN has no shell counters on it") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.NO_SHELL_COUNTERS_CONDITION;
        }
    },
    HasMinusOneCounter("it has a -1/-1 counter on it") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.HAS_MINUSONE_COUNTER_CONDITION;
        }
    },
    WarriorCardInGraveyard("a Warrior card is in your graveyard") {
        public MagicCondition toCondition(final Matcher arg) {
            return MagicCondition.WARRIOR_CARD_IN_GRAVEYARD_CONDITION;
        }
    }
    ;

    private final Pattern pattern;
    
    private MagicConditionParser(final String regex) {
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
    
    public Matcher matcher(final String rule) {
        return pattern.matcher(rule);
    }

    public abstract MagicCondition toCondition(final Matcher arg);
    
    public static final MagicCondition build(final String cost) {
        for (final MagicConditionParser rule : values()) {
            final Matcher matcher = rule.matcher(cost);
            if (matcher.matches()) {
                return rule.toCondition(matcher);
            }
        }
        throw new RuntimeException("Unable to match " + cost + " to a condition");
    }
}