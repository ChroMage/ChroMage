package chromage.client;

import chromage.shared.Entity;
import chromage.shared.GameState;
import chromage.shared.Mage;

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
                    state = GameState.deserializeFromString(output);
                    if (state.shouldTerminate()) {
                        for (Entity e : state.entities) {
                            if (e instanceof Mage) {
                                Mage m = (Mage)e;
                                if (!m.isDead()) {
                                    System.out.println(m.getName() + " is living at the end of the round.");
                                }
                            }
                        }
                        System.out.println("receive: exit.");
                        break;
                    }
                    if (state != null) {
                        //System.out.println("client: receive(x, y): " + state.x + ", " + state.y);
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
