package chromage.shared.utils;

/**
 * Created by ahruss on 3/13/15.
 */
public abstract class RateLimitedLoop implements Runnable {

    private long executionsPerSecond;
    private boolean shouldBreak;
    private Object result;

    public RateLimitedLoop(long rate) {
        executionsPerSecond = rate;
    }

    public void runInBackground() {
        new Thread() {
            public void run() {
                RateLimitedLoop.this.run();
            }
        }.start();
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object runAndGetResult() {
        run();
        return getResult();
    }

    public Object getResult() {
        if (result == null) {
            return defaultResult();
        } else {
            return result;
        }
    }

    public Object defaultResult() {
        return null;
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
