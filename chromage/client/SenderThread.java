package chromage.client;

import chromage.shared.Constants;
import chromage.shared.RateLimitedLoop;
import chromage.shared.UserInput;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ahruss on 3/13/15.
 */
public class SenderThread extends Thread {

    DataOutputStream output;
    public UserInput userInput = new UserInput();
    public boolean isRunning;

    public SenderThread(DataOutputStream output) {
        isRunning = true;
        this.output = output;
    }

    public void run() {

        try {
            output.writeBytes("new test 1 a\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
            public boolean shouldContinue() {
                return isRunning;
            }
            public void body() {
                try {
                    UserInput u = userInput;
                    output.writeBytes(u.serializeToString() + "\n");
                    System.out.println("sending: " + u.toString());
                    if (userInput.wantsTermination()) setBreak();
                } catch (Exception e) {
                    e.printStackTrace();
                    setBreak();
                }
            }
        }.run();
    }
}
