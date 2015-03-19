package chromage.client.util;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Listens to all keyboard events in a window. Used to make accessing keyboard input in the game
 * more user-friendly.
 *
 * Usage: every game tick, call Keyboard::poll(),
 * then get the status of the keys you care about with <code>Keyboard::isKeyDown</code> and <code>Keyboard::isFirstTickOfKey</code>
 */
public class Keyboard implements KeyListener {

    private static final int KEY_COUNT = 256;
    private static Keyboard  instance = null;
    /**
     * the current state of the keyboard
     */
    private boolean[] currentKeys = null;

    /**
     * the state of the keyboard at the last call to <code>poll()</code>
     */
    private KeyState[] keys = null;

    private Keyboard() {
        currentKeys = new boolean[KEY_COUNT];
        keys = new KeyState[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; ++i) {
            keys[i] = KeyState.RELEASED;
        }
    }

    private static Keyboard getInstance() {
        if (instance == null) {
            instance = new Keyboard ();
        }
        return instance;
    }

    /**
     * Subscribe to the key events for the given component
     * @param window the component to subscribe to key events for
     */
    public static void listenTo(Component window) {
        window.addKeyListener(getInstance());
    }

    /**
     * Find out if the given key is currently down
     * @param keyCode the keyCode to check
     * @return  true iff the key has been down for at least one tick
     */
    public static boolean isKeyDown(int keyCode) {
        return getInstance().keyDown(keyCode);
    }

    /**
     * Find out if the given key is down for the first tick
     * @param keyCode the key code to check
     * @return true iff the key has been down for exactly one tick
     */
    public static boolean isFirstTickOfKey(int keyCode) {
        return getInstance().keyDownOnce(keyCode);
    }

    /**
     * Update the state of the keyboard. Should be called once every game tick
     */
    public static synchronized void poll() {
        for (int i = 0; i < KEY_COUNT; ++i) {
            // Set the key state
            if (getInstance().currentKeys[i]) {
                // If the key is down now, but was not
                // down last frame, set it to ONCE,
                // otherwise, set it to PRESSED
                if (getInstance().keys[i] == KeyState.RELEASED)
                    getInstance().keys[i] = KeyState.ONCE;
                else
                    getInstance().keys[i] = KeyState.PRESSED;
            } else {
                getInstance().keys[i] = KeyState.RELEASED;
            }
        }
    }

    private boolean keyDown(int keyCode) {
        return keys[keyCode] == KeyState.ONCE ||
                keys[keyCode] == KeyState.PRESSED;
    }

    private boolean keyDownOnce(int keyCode) {
        return keys[keyCode] == KeyState.ONCE;
    }

    public synchronized void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            currentKeys[keyCode] = true;
        }
    }

    public synchronized void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode >= 0 && keyCode < KEY_COUNT) {
            currentKeys[keyCode] = false;
        }
    }

    public void keyTyped(KeyEvent e) {
        // Not needed
    }

    private enum KeyState {
        /**
         * Not down
         */
        RELEASED,
        /**
         * Down, but not the first time
         */
        PRESSED,
        /**
         * Down for the first time
         */
        ONCE
    }
}

