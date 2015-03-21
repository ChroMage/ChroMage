package chromage.ai;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import chromage.server.Player;
import chromage.shared.Mage;
import chromage.shared.engine.GameState;
import chromage.shared.utils.UserInput;
import chromage.shared.utils.UserInput.HorizontalDirection;
import chromage.shared.utils.UserInput.SpellInput;
import chromage.shared.utils.UserInput.VerticalDirection;

public class AIPlayer implements Player {
	GameState state;
	 /**
     * The most recent input state made.
     */
	UserInput input;
	/**
     * The mage associated with this player
     */
    public Mage mage;
	
    public AIPlayer() {
		mage = new Mage(Mage.Type.PURPLE);
		input = new UserInput();
	}
    
	@Override
	public UserInput getCurrentInputState() {
		Strategy proStrat = getStrategy();
		if (state != null){
			input = proStrat.makeDecisions(state, mage);
		}
		return input;
	}

	private Strategy getStrategy() {
		return new AggressiveStrategy();
	}
	
	public static interface Strategy {
		public UserInput makeDecisions(GameState state, Mage mage);
		public VerticalDirection getVerticalDirection();
		public HorizontalDirection getHorizontalDirection();
		public SpellInput getSpell();
		public Point2D.Double getTargetLocation();
	}

	@Override
	public void sendUpdate(GameState state) {
		this.state = state;
	}

	@Override
	public Mage getMage() {
		return mage;
	}

	@Override
	public boolean wantsTermination() {
		return false;
	}

	@Override
	public void join() throws InterruptedException {
		
	}

	
}
