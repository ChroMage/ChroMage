package chromage.shared.utils;

import java.util.UUID;

public class GameInfo {
    private String name;
    private UUID id;
    private int numberOfPlayers;
    private int expectedNumberOfPlayers;

    public GameInfo() {
        name = "Test";
        id = UUID.randomUUID();
        numberOfPlayers = 1;
    }

    public static GameInfo parse(String g) {
        String[] parts = g.split(" ");
        GameInfo game = new GameInfo();
        game.id = UUID.fromString(parts[0]);
        game.name = parts[1];
        game.numberOfPlayers = Integer.parseInt(parts[2]);
        game.expectedNumberOfPlayers = Integer.parseInt(parts[3]);
        return game;
    }

    public String getGameName() {
        return name;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public String toString() {
        return name + " (" + numberOfPlayers + " / " + expectedNumberOfPlayers + " players)";
    }

    public UUID getUuid() {
        return id;
    }

    public int getExpectedNumberOfPlayers() {
        return expectedNumberOfPlayers;
    }
}
