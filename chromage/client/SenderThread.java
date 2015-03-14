package chromage.client;

import chromage.shared.Constants;
import chromage.shared.RateLimitedLoop;

import java.io.DataOutputStream;
import java.io.IOException;

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

        try {
            output.writeBytes("new test 1 red-blue");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
            public void body() {
                try {
                    output.writeBytes(keyState + "\n");
                    System.out.println("sending: " + keyState);
                } catch (Exception e) {
                    e.printStackTrace();
                    setBreak();
                }
            }
        }.run();
    }
}
