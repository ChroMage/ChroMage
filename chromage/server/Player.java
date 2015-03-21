package chromage.server;

import chromage.shared.Mage;
import chromage.shared.engine.GameState;
import chromage.shared.utils.UserInput;

public interface Player {
	UserInput getCurrentInputState();
	void sendUpdate(GameState state);
	Mage getMage();
	boolean wantsTermination();
	void join() throws InterruptedException;
}
