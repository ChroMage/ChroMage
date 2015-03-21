package chromage.shared.engine;

import chromage.shared.Mage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Holds the current state of the game.
 */
public class GameState implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
    /**
     * The number of players we are waiting on to join the game.
     */
    private int awaitedPlayers;

    /**
     * The collection of all entities in the game world which are still active
     */
    private ArrayList<Entity> entities;

    /**
     * The number of times <code>update()</code> has been called.
     */
    private long currentTick;

    /**
     * Flag to be set true if the game is over, indicates to the client it should exit gracefully
     */
    private boolean shouldTerminate;

    /**
     * true if the game should be over even if there are still multiple players living.
     */
    private boolean isGameEndedExternally;

    public GameState() {
        entities = new ArrayList<Entity>();
        shouldTerminate = false;
        isGameEndedExternally = false;
    }

    /**
     * @return false iff the game should continue running.
     */
    public boolean isGameOver() {
        if (isGameEndedExternally)
            return true;
        // check if there are at least two living players could do getLivingPlayers().size() > 1, but
        // this way we avoid iterating over all entities, which should make it slightly faster.
        boolean playerFound = false;
        for (Entity e : entities) {
            if (e instanceof Mage && !((Mage) e).isDead()) {
                // if this is the second player we've found, there are at least two players living
                if (playerFound) return false;
                playerFound = true;
            }
        }
        // if we got through all the entities and didn't find two players, the game must be over.
        return true;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameEndedExternally = gameOver;
    }

    /**
     * @return the collection of mages who are not yet dead 
     */
    public ArrayList<Mage> getLivingPlayers() {
        ArrayList<Mage> mages = new ArrayList<Mage>();
        for (Entity e : entities) {
            if (e instanceof Mage) {
                Mage m = (Mage) e;
                if (!m.isDead()) {
                    mages.add(m);
                }
            }
        }
        return mages;
    }

    /**
     * Prepare the map before the game starts
     *
     * @param players the players who will take part in this game
     */
    public void initialize(ArrayList<Mage> players) {
        generateMap();
        spreadPlayers(players);
        for (Mage m : players) {
            entities.add(m);
        }
        if (players.size() < 2) {
            //Add a dummy mage to fight
            entities.add(new Mage(2000, 2000, 100, 200, Color.BLUE, "Bot"));
        }
    }

    /**
     * Do everything that should happen each tick. Should be called externally exactly once per tick.
     */
    public void update() {
        ArrayList<Entity> toAdd = new ArrayList<Entity>();
        for (Entity e : entities) {
            toAdd.addAll(e.update(getEntities()));
        }
        checkCollisions();
        entities.removeAll(getEntitiesToRemove());
        entities.addAll(toAdd);
        currentTick++;
    }

    public void checkCollisions() {
        for (Entity a : entities) {
            for (Entity b : entities) {
                if (a.canCollideWith(b) && a.getHitbox().intersects(b.getHitbox())) {
                    a.didCollideWith(b);
                }
            }
        }
    }

    /**
     * @return the list of entities which have been set to be removed from the game
     */
    public ArrayList<Entity> getEntitiesToRemove() {
        ArrayList<Entity> toRemove = new ArrayList<Entity>();
        for (Entity e : entities) {
            if (e.shouldBeRemoved()) toRemove.add(e);
        }
        return toRemove;
    }

    /**
     * @return an immutable collection of all the entities in the game
     */
    public Collection<Entity> getEntities() {
        return Collections.unmodifiableCollection(entities);
    }

    public long getCurrentTick() {
        return currentTick;
    }

    /**
     * Move the players about the map randomly
     *
     * @param players the list of players to randomly spread
     */
    public void spreadPlayers(ArrayList<Mage> players) {
        for (Mage m : players) {
            Random r = new Random();
            m.setPosition(new Point2D.Double(500 + (int) r.nextInt(3000), 1000));
        }
    }

    /**
     * Generate all the blocks in the map
     */
    private void generateMap() {
        // Add border blocks
        entities.add(new Block(-10, 3700, 4010, 310));
        entities.add(new Block(-10, -10, 4010, 310));
        entities.add(new Block(-10, -10, 310, 4010));
        entities.add(new Block(3700, -10, 310, 4010));
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void setTerminate() {
        this.shouldTerminate = true;
    }

    public int getAwaitedPlayers() {
        return awaitedPlayers;
    }

    public void setAwaitedPlayers(int awaitedPlayers) {
        this.awaitedPlayers = awaitedPlayers;
    }
}