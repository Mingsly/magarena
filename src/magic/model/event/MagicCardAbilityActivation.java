package magic.model.event;

import magic.model.MagicCard;
import magic.model.MagicGame;
import magic.model.MagicPayedCost;
import magic.model.MagicSource;
import magic.model.ARG;
import magic.model.action.MagicPutItemOnStackAction;
import magic.model.choice.MagicChoice;
import magic.model.condition.MagicCondition;
import magic.model.stack.MagicAbilityOnStack;

import java.util.List;
import java.util.LinkedList;

public abstract class MagicCardAbilityActivation extends MagicCardActivation {

    final String name;

    public MagicCardAbilityActivation(final MagicCondition[] conditions, final MagicActivationHints hints, final String aName) {
        super(
            conditions,
            hints,
            aName
        );
        name = aName;
    }
    
    public MagicCardAbilityActivation(final MagicActivationHints hints, final String aName) {
        this(MagicActivation.NO_COND, hints, aName);
    }

    public abstract MagicEvent getCardEvent(final MagicCard card, final MagicPayedCost payedCost);

    @Override
    public MagicEvent getEvent(final MagicSource source) {
        return new MagicEvent(
            source,
            new MagicEventAction() {
                @Override
                public void executeEvent(final MagicGame game, final MagicEvent event) {
                    final MagicAbilityOnStack abilityOnStack = new MagicAbilityOnStack(
                        MagicCardAbilityActivation.this,
                        getCardEvent(event.getCard(), game.getPayedCost())
                    );
                    game.doAction(new MagicPutItemOnStackAction(abilityOnStack));
                }
            },
            name + " SN."
        );
    }

    @Override
    final MagicChoice getChoice(final MagicCard source) {
        return getCardEvent(source, MagicPayedCost.NO_COST).getChoice();
    }
    
    public static final MagicCardAbilityActivation create(final String act, final String desc) {
        final String[] token = act.split(ARG.COLON, 2);
        
        final String costs = token[0];
        final List<MagicMatchedCostEvent> matchedCostEvents = MagicMatchedCostEvent.build(costs);
        assert matchedCostEvents.size() > 0;
        
        final String rule = token[1];
        final MagicSourceEvent sourceEvent = MagicRuleEventAction.create(rule);

        boolean isIndependent = sourceEvent.isIndependent();
        for (final MagicMatchedCostEvent matched : matchedCostEvents) {
            isIndependent &= matched.isIndependent();
        }

        return new MagicCardAbilityActivation(
            sourceEvent.getConditions(),
            new MagicActivationHints(
                sourceEvent.getTiming(),
                isIndependent
            ),
            desc
        ) {
            @Override
            public Iterable<? extends MagicEvent> getCostEvent(final MagicCard source) {
                final List<MagicEvent> costEvents = new LinkedList<MagicEvent>();
                for (final MagicMatchedCostEvent matched : matchedCostEvents) {
                    costEvents.add(matched.getEvent(source));
                }
                return costEvents;
            }
       
            @Override
            public MagicEvent getCardEvent(final MagicCard source, final MagicPayedCost payedCost) {
                return sourceEvent.getEvent(source);
            }
        };
    }
}