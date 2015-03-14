package chromage.client;

import java.util.UUID;

public class GameInfo {
    private String name;
    private UUID id;
    private int numberOfPlayers;

    public static GameInfo parse(String g) {
        String[] parts = g.split(" ");
        GameInfo game = new GameInfo();
        game.id = UUID.fromString(parts[0]);
        game.name = parts[1];
        game.numberOfPlayers = Integer.parseInt(parts[2]);
        return game;
    }

    public String toString() {
        return name + " (" + numberOfPlayers + " players)";
    }

    public UUID getUuid() {
        return id;
    }
}
