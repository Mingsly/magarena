package magic.model.target;

import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.MagicPlayer;

public class MagicPTTargetFilter extends MagicPermanentFilterImpl {

    private final MagicPermanentFilterImpl targetFilter;
    private final Operator pOp;
    private final int power;
    private final Operator tOp;
    private final int toughness;
    
    public MagicPTTargetFilter(final MagicPermanentFilterImpl aTargetFilter, final int power) {
        this(aTargetFilter, Operator.LESS_THAN_OR_EQUAL, power, Operator.ANY, 0);
    }
    
    public MagicPTTargetFilter(final MagicPermanentFilterImpl aTargetFilter, final Operator powerOp, final int power) {
        this(aTargetFilter, powerOp, power, Operator.ANY, 0);
    }

    public MagicPTTargetFilter(final MagicPermanentFilterImpl aTargetFilter, 
        final Operator aPowerOp, final int aPower,
        final Operator aToughnessOp, final int aToughness) {
        assert aTargetFilter != null;
        targetFilter = aTargetFilter;
        pOp = aPowerOp;
        power = aPower;
        tOp = aToughnessOp;
        toughness = aToughness;
    }
    @Override
    public boolean accept(final MagicGame game,final MagicPlayer player,final MagicPermanent target) {
        return targetFilter.accept(game,player,target) &&
               pOp.cmp(target.getPower(), power) &&
               tOp.cmp(target.getToughness(), toughness);
    }
    @Override
    public boolean acceptType(final MagicTargetType targetType) {
        return targetFilter.acceptType(targetType);
    }
};
