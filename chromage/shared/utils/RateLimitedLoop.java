package chromage.shared.utils;

/**
 * A class to enable executing the same section of code repeatedly at a given frequency.
 *
 * Conceptually, its implementation is the same as:
 *
 *     while (shouldContinue()) { body(); }
 *
 * Also supports yielding a result via setResult() in the body
 */
public abstract class RateLimitedLoop implements Runnable {

    private long executionsPerSecond;
    private boolean shouldBreak;
    private Object result;

    /**
     * @param rate  the number of executions of this loop desired per second
     */
    public RateLimitedLoop(long rate) {
        executionsPerSecond = rate;
    }

    /**
     * Creates a background thread and runs the loop on it
     * @return  the thread running the loop in the background
     */
    public Thread runInBackground() {
        Thread t = new Thread() {
            public void run() {
                RateLimitedLoop.this.run();
            }
        };
        t.start();
        return t;
    }

    /**
     * Run the loop and return the value of result after it's done
     * @return  the value of result after the loop is done
     */
    public Object runAndGetResult() {
        run();
        return getResult();
    }

    /**
     * Gets result or the default value if it's not set
     * @return
     */
    public Object getResult() {
        if (result == null) {
            return defaultResult();
        } else {
            return result;
        }
    }

    /**
     * Set the result of the loop
     * @param result
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * The default result if none is given
     * @return
     */
    public Object defaultResult() {
        return null;
    }

    /**
     * The body of the loop
     */
    public abstract void body();

    /**
     * If called, this iteration of the loop will be the last one.
     */
    protected void setBreak() {
        shouldBreak = true;
    }

    /**
     * Called every iteration of the loop. If true, the loop keeps running; if false, the loop is done.
     */
    public boolean shouldContinue() {
        return !shouldBreak;
    }

    @Override
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
