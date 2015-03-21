package chromage.shared.utils;

public class Constants {
    /**
     * The battlefield height in game points
     */
    public static final int BATTLEFIELD_HEIGHT = 4000;
    /**
     * the battlefield width in game points
     */
    public static final int BATTLEFIELD_WIDTH = 4000;
    /**
     * The desired number of ticks per second in the game
     */
    public static final long TICKS_PER_SECOND = 60L;

    /**
     * collision bitmask for mage
     */
    public static final int MAGE_TYPE = 1 << 0;
    /**
     * collision bitmask for blocks
     */
    public static final int BLOCK_TYPE = 1 << 1;
    /**
     * collision bitmask for projectiles
     */
    public static final int PROJECTILE_TYPE = 1 << 2;
    /**
     * How long to wait before stopping the last input with lag
     */
    public static final int INPUT_TIMEOUT_TICKS = 6;
}
