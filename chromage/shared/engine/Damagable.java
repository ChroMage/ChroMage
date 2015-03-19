package chromage.shared.engine;

/**
 * An object which can take or heal damage
 */
public interface Damagable {
    /**
     * Remove the given amount of health from the object
     *
     * @param damage the damage to take
     */
    public void takeDamage(int damage);

    /**
     * Add the given amount of health back to the object
     *
     * @param damage the damage to heal
     */
    public void healDamage(int damage);

    /**
     * @return the amount of damage the object can take before it is destroyed
     */
    public int getHealth();
}
