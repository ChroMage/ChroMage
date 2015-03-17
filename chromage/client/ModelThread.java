package chromage.client;

import chromage.shared.GameState;

import java.io.BufferedReader;

/**
 * Created by ahruss on 3/13/15.
 */
public class ModelThread extends Thread {

    public GameState state;
    BufferedReader input;

    public ModelThread(BufferedReader input) {
        state = new GameState();
        this.input = input;
    }

    public void run() {
        try {

            // ignore the handshake
            input.readLine();
            System.out.println("Handshake successful");

            String joined = input.readLine();
//            if (!"success".equals(joined)) {
//                throw new Exception();
//            }

            while (true) {
                try {
                    String output = input.readLine();
                    //System.out.println(output);
                    state = GameState.deserializeFromString(output);
                    if (state != null) {
                        //System.out.println("client: receive(x, y): " + state.x + ", " + state.y);
                    }
                    if (state == null) {  //if bye or terminate received, end connection
                        System.out.println("receive: exit.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
