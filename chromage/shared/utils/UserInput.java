package chromage.shared.utils;

import chromage.shared.engine.HorizontalDirection;
import chromage.shared.spells.SpellInput;
import chromage.shared.engine.VerticalDirection;

import javax.xml.bind.DatatypeConverter;
import java.awt.geom.Point2D;
import java.io.*;

public class UserInput implements Serializable {
	public Point2D.Double mouseLocation;
	public HorizontalDirection horizontalDirection;
	public VerticalDirection verticalDirection;
	public SpellInput spell;
	public boolean wantsTermination;

	public boolean wantsTermination() {
		return wantsTermination;
	}

	public void setWantsTermination(boolean wantsTermination) {
		this.wantsTermination = wantsTermination;
	}

	public UserInput() {
		mouseLocation = new Point2D.Double(0.0, 0.0);
		horizontalDirection = HorizontalDirection.NONE;
		verticalDirection = VerticalDirection.NONE;
		spell = SpellInput.NONE;
	}

	public String serializeToString() throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream so = new ObjectOutputStream(bo);
		so.writeObject(this);
		so.flush();
		return DatatypeConverter.printBase64Binary(bo.toByteArray());
	}

	public static UserInput deserializeFromString(String s) throws ClassNotFoundException, IOException {
		byte b[] = DatatypeConverter.parseBase64Binary(s);
		ByteArrayInputStream bi = new ByteArrayInputStream(b);
		ObjectInputStream si = new ObjectInputStream(bi);
		return (UserInput)si.readObject();
	}

	public String toString() {
		return "UserInput{spell " + spell + ", v " + verticalDirection + ", h " + horizontalDirection + "}";
	}
}
