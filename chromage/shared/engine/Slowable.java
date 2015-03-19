package chromage.shared.engine;

/**
 * Created by ahruss on 3/19/15.
 */
public interface Slowable {
    public void slowBy(int amount);

    public double getSlowAmount();
}
