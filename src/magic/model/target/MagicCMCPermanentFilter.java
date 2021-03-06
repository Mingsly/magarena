package magic.model.target;

import magic.model.MagicGame;
import magic.model.MagicPermanent;
import magic.model.MagicPlayer;

public class MagicCMCPermanentFilter extends MagicPermanentFilterImpl {

    private final MagicTargetFilter<MagicPermanent> targetFilter;
    private final Operator operator;
    private final int cmc;

    public MagicCMCPermanentFilter(final MagicTargetFilter<MagicPermanent> targetFilter,final Operator operator,final int cmc) {
        this.targetFilter = targetFilter;
        this.operator = operator;
        this.cmc = cmc;
    }

    @Override
    public boolean accept(final MagicGame game,final MagicPlayer player,final MagicPermanent target) {
        return targetFilter.accept(game,player,target) &&
               operator.cmp(target.getConvertedCost(), cmc) ;
    }

    @Override
    public boolean acceptType(final MagicTargetType targetType) {
        return targetFilter.acceptType(targetType);
    }
}
