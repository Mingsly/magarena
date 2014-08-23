package magic.model.event;

import magic.model.MagicCounterType;
import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.MagicSource;
import magic.model.action.MagicChangeCountersAction;
import magic.model.action.MagicPermanentAction;
import magic.model.choice.MagicTargetChoice;
import magic.model.condition.MagicCondition;
import magic.model.condition.MagicConditionFactory;
import magic.model.target.MagicTargetFilterFactory.Control;
import magic.model.target.MagicTargetFilterFactory;

public class MagicRemoveCounterChosenEvent extends MagicEvent {

    private final MagicCondition[] conds;

    public MagicRemoveCounterChosenEvent(final MagicSource source, final MagicCounterType counterType) {
        super(
            source,
            new MagicTargetChoice(
                MagicTargetFilterFactory.creature(counterType, Control.You),
                "a creature you control with a " + counterType.getName() + " counter on it"
            ),
            new MagicEventAction() {
                @Override
                public void executeEvent(final MagicGame game, final MagicEvent event) {
                    event.processTargetPermanent(game, new MagicPermanentAction() {
                        public void doAction(final MagicPermanent perm) {
                            game.doAction(new MagicChangeCountersAction(
                                perm,
                                counterType,
                                -1,
                                true
                            ));
                        }
                    });
                }
            },
            "Remove a " + counterType.getName() + " counter from a creature$ you control."
        );
        conds = new MagicCondition[]{
            MagicConditionFactory.HasOptions(getTargetChoice())
        };
    }

    @Override
    public final MagicCondition[] getConditions() {
        return conds;
    }
}