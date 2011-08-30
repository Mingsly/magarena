package magic.model.action;

import magic.ai.ArtificialScoringSystem;
import magic.model.MagicGame;
import magic.model.MagicPermanent;

/**
 * Unattaches equipment from currently equipped creature.
 * Attaches equipment to new creature when not null.
 */
public class MagicAttachEquipmentAction extends MagicAction {

	private final MagicPermanent equipment;
	private final MagicPermanent creature;
	private MagicPermanent oldEquippedCreature;
	private boolean validEquipment;
	private boolean validCreature;
	
	public MagicAttachEquipmentAction(final MagicPermanent equipment,final MagicPermanent creature) {
		this.equipment=equipment;
		this.creature=creature;
	}
	
	@Override
	public void doAction(final MagicGame game) {
		validEquipment=equipment.getController().controlsPermanent(equipment);
		if (!validEquipment) {
			return;
		}
		int score=ArtificialScoringSystem.getTurnScore(game);
		oldEquippedCreature=equipment.getEquippedCreature();
		if (oldEquippedCreature!=null) {
			score-=oldEquippedCreature.getScore(game);
			oldEquippedCreature.removeEquipment(equipment);
			score+=oldEquippedCreature.getScore(game);
			if (oldEquippedCreature.getController()==equipment.getController()) {
				// Prevent unnecessary equips.
				if (oldEquippedCreature==creature) {
					score+=ArtificialScoringSystem.UNNECESSARY_EQUIP_SCORE;
				} else {
					score+=ArtificialScoringSystem.UNEQUIP_SCORE;
				}
			} else {
				score=-score;
			}
		}
		validCreature=creature!=null&&creature.getController().controlsPermanent(creature);
		if (validCreature) {
			score-=creature.getScore(game);
			equipment.setEquippedCreature(creature);
			creature.addEquipment(equipment);
			score+=creature.getScore(game);
		} else {
			equipment.setEquippedCreature(null);
		}
		setScore(equipment.getController(),score);		
		game.setStateCheckRequired();
	}

	@Override
	public void undoAction(final MagicGame game) {
		
		if (validEquipment) {
			if (validCreature) {
				creature.removeEquipment(equipment);
			}
			equipment.setEquippedCreature(oldEquippedCreature);
			if (oldEquippedCreature!=null) {
				oldEquippedCreature.addEquipment(equipment);
			}
		}
	}
}
