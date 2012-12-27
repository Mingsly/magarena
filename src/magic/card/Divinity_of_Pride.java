package magic.card;

import magic.model.MagicAbility;
import magic.model.MagicPermanent;
import magic.model.MagicPowerToughness;
import magic.model.mstatic.MagicLayer;
import magic.model.mstatic.MagicStatic;

public class Divinity_of_Pride {
    public static final MagicStatic S1 = new MagicStatic(MagicLayer.ModPT) {
        @Override
        public void modPowerToughness(final MagicPermanent source,final MagicPermanent permanent,final MagicPowerToughness pt) {
            if (permanent.getController().getLife() >= 25) {
                pt.add(4,4);
            }
        }
    };
    
//    public static final MagicStatic S2 = new MagicStatic(MagicLayer.Ability) {
//        @Override
//        public long getAbilityFlags(final MagicPermanent source,final MagicPermanent permanent,final long flags) {
//            return permanent.getController().getLife() >= 30 ?
//                flags|MagicAbility.Flying.getMask() : flags;
//        }
//    };
}
