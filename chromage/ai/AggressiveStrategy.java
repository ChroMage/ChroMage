package chromage.ai;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import chromage.ai.AIPlayer.Strategy;
import chromage.shared.Mage;
import chromage.shared.engine.GameState;
import chromage.shared.spells.FruitPunch;
import chromage.shared.utils.UserInput;
import chromage.shared.utils.UserInput.HorizontalDirection;
import chromage.shared.utils.UserInput.SpellInput;
import chromage.shared.utils.UserInput.VerticalDirection;
import chromage.shared.utils.Utilities;

public class AggressiveStrategy implements Strategy {

	private GameState state;
	private Mage mage;
	private UserInput input = new UserInput();

	@Override
	public VerticalDirection getVerticalDirection() {
		return VerticalDirection.NONE;
	}

	@Override
	public HorizontalDirection getHorizontalDirection() {
		Mage closestEnemy = Utilities.getClosestEnemy(mage, state.getLivingPlayers());
		if (closestEnemy.getCenter().x > mage.getCenter().x){
			return HorizontalDirection.RIGHT;
		}
		return HorizontalDirection.LEFT;
	}

	@Override
	public SpellInput getSpell() {
		Mage closestEnemy = Utilities.getClosestEnemy(mage, state.getLivingPlayers());
		//If you are close to the enemy X position, FRUIT PUNCH THEM TO THE SKY
		if (Math.abs(closestEnemy.getCenter().x - mage.getCenter().x) < (new FruitPunch()).getWidth()){
			return SpellInput.LEFT;
		}
		//else blink
		return SpellInput.MIDDLE;
	}

	@Override
	public Double getTargetLocation() {
		if (state == null) return new Point2D.Double(0,0);
		Mage closestEnemy = Utilities.getClosestEnemy(mage, state.getLivingPlayers());
		return Utilities.addAll(closestEnemy.getCenter(), closestEnemy.getVelocity(), Utilities.invert(mage.getVelocity())) ;
	}

	@Override
	public UserInput makeDecisions(GameState state, Mage mage) {
		this.state = state;
		this.mage = mage;

		input.mouseLocation = getTargetLocation();
		input.horizontalDirection = getHorizontalDirection();
		input.verticalDirection = getVerticalDirection();
		input.spell = getSpell();
		
		return input;
	}

}
