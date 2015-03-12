package chromage.client;

import java.io.DataOutputStream;

/**
 * Created by ahruss on 3/13/15.
 */
public class SenderThread extends Thread {

    DataOutputStream output;
    public int keyState;
    public boolean isRunning;

    public SenderThread(DataOutputStream output) {
        keyState = 0;
        isRunning = true;
        this.output = output;
    }

    public void run() {
        int desiredTickLengthMillis = 1000 / 60;

        while (isRunning) {
            long startTime = System.currentTimeMillis();
            try {
                output.writeBytes(keyState + "\n");
                System.out.println("sending: " + keyState);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
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
