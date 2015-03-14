package chromage.shared;

import java.awt.geom.Point2D;

public class UserInput {
	public Point2D point2D;
	public HorizontalDirection horizontalDirection;
	public VerticalDirection verticalDirection;
	public Spell spell;

	public UserInput() {
		point2D = new Point2D.Double(0.0, 0.0);
		horizontalDirection = HorizontalDirection.NONE;
		verticalDirection = VerticalDirection.NONE;
		spell = Spell.NONE;
	}
}
