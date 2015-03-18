package chromage.client.util;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.prefs.Preferences;

/**
 * Created by ahruss on 3/18/15.
 */
public class Configuration {

    public static class Preference<T extends Serializable> {
        private String key;
        private T defaultValue;

        public Preference(String key, T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public T get() {
            try {
                return deserializeFromString(Configuration.get(key, serializeToString(defaultValue)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void set(T newValue) {
            try {
                Configuration.put(key, serializeToString(newValue));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
}
