package chromage.shared.engine;

/**
 * Visitor for processing collisions. Implementers should assume visited nodes will call all appropriate processing
 * methods for their type.
 */
public interface CollisionProcessor {
    public void processCollision(Damagable d);

    public void processCollision(Comboable c);

    public void processCollision(Slowable c);

    public void processCollision(Entity e);

    public void processCollision(MobileEntity e);

    public void processCollision(Block b);
}
