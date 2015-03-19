package chromage.shared.utils;

import java.awt.geom.Point2D;
import java.io.*;

/**
 * Stores user input for the game
 */
public class UserInput implements Serializable {
    public Point2D.Double mouseLocation;
    public HorizontalDirection horizontalDirection;
    public VerticalDirection verticalDirection;
    public SpellInput spell;
    /**
     * true if the player has pressed the exit button during this game session
     */
    public boolean wantsTermination;

    public UserInput() {
        mouseLocation = new Point2D.Double(0.0, 0.0);
        horizontalDirection = HorizontalDirection.NONE;
        verticalDirection = VerticalDirection.NONE;
        spell = SpellInput.NONE;
    }

    public boolean wantsTermination() {
        return wantsTermination;
    }

    public void setWantsTermination(boolean wantsTermination) {
        this.wantsTermination = wantsTermination;
    }

    public String toString() {
        return "UserInput{spell " + spell + ", v " + verticalDirection + ", h " + horizontalDirection + "}";
    }

    public static enum HorizontalDirection {
        LEFT(),
        NONE(),
        RIGHT()
    }

    public static enum VerticalDirection {
        JUMP(),
        NONE()
    }

    public static enum SpellInput {
        LEFT(),
        MIDDLE(),
        RIGHT(),
        NONE
    }
}
