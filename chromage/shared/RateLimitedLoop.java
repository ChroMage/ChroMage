package chromage.shared;

/**
 * Created by ahruss on 3/13/15.
 */
public abstract class RateLimitedLoop implements Runnable {

    private long executionsPerSecond;
    private boolean shouldBreak;

    public RateLimitedLoop(long rate) {
        executionsPerSecond = rate;
    }

    public abstract void body();

    protected void setBreak() {
        shouldBreak = true;
    }

    public boolean shouldContinue() {
        return !shouldBreak;
    }

    public void run() {
        long desiredTickLengthMillis = 1000 / executionsPerSecond;
        while (shouldContinue() && !shouldBreak) {
            long startTime = System.currentTimeMillis();

            body();

            // wait until enough time has passed to make the game 60 tps
            long endTime = System.currentTimeMillis();
            if (endTime - startTime < desiredTickLengthMillis) {
                try {
                    Thread.sleep(desiredTickLengthMillis - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
