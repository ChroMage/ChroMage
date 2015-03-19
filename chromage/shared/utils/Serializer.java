package chromage.shared.utils;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * Handles serialization of objects to and from strings
 */
public class Serializer {

    /**
     * Deserialize an object from a base64 string using the default serialization
     *
     * @param s   the string serialization
     * @param <T> the type of the object to deserialize
     * @return the object deserialized
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static <T extends Serializable> T deserializeFromString(String s) throws ClassNotFoundException, IOException {
        byte b[] = DatatypeConverter.parseBase64Binary(s);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si = new ObjectInputStream(bi);
        return (T) si.readObject();
    }

    /**
     * Serialize an object to base64 string
     *
     * @param object the object to serialize
     * @param <T>    the type of the object to serialize
     * @return the serialization
     * @throws IOException
     */
    public static <T> String serializeToString(T object) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream so = new ObjectOutputStream(bo);
        so.writeObject(object);
        so.flush();
        return DatatypeConverter.printBase64Binary(bo.toByteArray());
    }
}
