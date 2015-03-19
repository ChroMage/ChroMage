package chromage.client.util;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.prefs.Preferences;

/**
 * Handles storage and retrieval of user preferences. Internally uses java.util.prefs.Preferences
 */
public class Configuration {

    public static final Preference<String> SERVER_IP = new Preference<String>("com.chromage.serverIp", "127.0.0.1");
    public static final Preference<Integer> SERVER_PORT = new Preference<Integer>("com.chromage.serverPort", 9877);
    public static final Preference<String> GAME_NAME = new Preference<String>("com.chromage.gameName", "ChroMage Game");
    public static final Preference<String> PLAYER_NAME = new Preference<String>("com.chromage.playerName", "Player");
    public static final Preference<Integer> GAME_SIZE = new Preference<Integer>("com.chromage.gameSize", 2);
    private static Preferences instance;

    public static Preferences getInstance() {
        if (instance == null) {
            instance = Preferences.userNodeForPackage(chromage.client.Client.class);
        }
        return instance;
    }

    public static String get(String key, String defaultValue) {
        return getInstance().get(key, defaultValue);
    }

    public static void put(String key, String value) {
        getInstance().put(key, value);
    }

    /**
     * An individual preference to be stored.
     * @param <T> The type of the stored preference
     */
    public static class Preference<T extends Serializable> {

        /**
         * The key in the preference dictionary
         */
        private String key;

        /**
         * The value of the preference if none has been given yet.
         */
        private T defaultValue;

        /**
         * Create a new preference object with the given default value and key
         * @param key
         * @param defaultValue
         */
        public Preference(String key, T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        /**
         * Get the preference from storage
         * @return the value stored or default value if there was none. Also returns the default if
         *          something goes wrong trying to retrieve the value
         */
        public T get() {
            try {
                return deserializeFromString(Configuration.get(key, serializeToString(defaultValue)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return defaultValue;
        }

        /**
         * Save the value of this preference
         * @param newValue the new value to save
         */
        public void set(T newValue) {
            try {
                Configuration.put(key, serializeToString(newValue));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Serialize the given value to a string. If we know of a human-readable way to
         * perform the serialization, prefer that; otherwise, use base 64.
         * @param value the value to serialize
         * @return the string representation of value, which can be deserialized with <code>Preference::deserializeFromString</code>
         * @throws IOException if something goes wrong serializing the object
         */
        public String serializeToString(T value) throws IOException {
            if (value instanceof String) {
                return (String)value;
            } else if (value instanceof Integer) {
                return Integer.toString((Integer)value);
            } else {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream so = new ObjectOutputStream(bo);
                so.writeObject(value);
                so.flush();
                return DatatypeConverter.printBase64Binary(bo.toByteArray());
            }
        }

        /**
         * The inverse operation of <code>Preference::serializeToString</code>
         * @param s     the string representation of the object to deserialize
         * @return      the deserialized object
         * @throws ClassNotFoundException
         * @throws IOException
         */
        public T deserializeFromString(String s) throws ClassNotFoundException, IOException {
            if (defaultValue instanceof String) {
                return (T)s;
            } else if (defaultValue instanceof Integer) {
                try {
                    return (T) ((Integer) Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            } else {
                byte b[] = DatatypeConverter.parseBase64Binary(s);
                ByteArrayInputStream bi = new ByteArrayInputStream(b);
                ObjectInputStream si = new ObjectInputStream(bi);
                return (T) si.readObject();
            }
        }
    }
}
