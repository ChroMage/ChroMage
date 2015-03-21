package chromage.shared.utils;

/**
 * Created by ahruss on 3/19/15.
 */
public class Timer {
    public static void time(String label, Runnable r) {
        long startTime = System.nanoTime();
        r.run();
        long endTime = System.nanoTime();
        //System.out.println(label + " took " + ((endTime - startTime) / (1000.0 * 1000.0 * 1000.0)));
    }
}
