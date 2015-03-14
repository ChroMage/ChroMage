package chromage.client;

import chromage.shared.MageType;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ahruss on 3/14/15.
 */
public interface ILobbyMenuDelegate {

    public void joinGame(UUID id, MageType mageType);
    public ArrayList<GameInfo> getGameList();
    public void createGame(int numberOfPlayers, String name, MageType mageType);
}
