package chromage.shared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Mage extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

	//added
	private boolean secondJump = false;
	private boolean firstJump = false;
	private double slowAmount = 1.0;
	public static final int DEFAULT_HEIGHT = 300;
	public static final int DEFAULT_WIDTH = 100;
	public static final int MAX_HP = 1000;
	public Point2D.Double getVelocity() {
		return new Point2D.Double(velocity.x*slowAmount, velocity.y*slowAmount);
	}
	private int coolDown = 0;
	public int hp = MAX_HP;
	public int mana = 300;
	private int combo;
	private String name = "Training Bot";
	public Spell leftSpell = new Fireball();
	public Spell middleSpell = new Blink();
	public Spell rightSpell = new Iceball();
	public MageType mageType;
	
	public boolean isDead() {
		return hp == 0;
	}

	public void takeDamage(int dmg, int slowAmount, int comboValue) {
		hp -= getDamageWithCombo(dmg, combo);
		System.out.println("DAMAGE: " + getDamageWithCombo(dmg, combo) + ". HP: " + hp + "COMBO: " + combo);
		combo += comboValue;
		this.slowAmount = slowAmount/100.0;
		if(hp <= 0) {
			hp = 0;
			this.setShouldBeRemoved(true);
		}
	}
	
	private int getDamageWithCombo(int damage, int combo) {
		return (int) (damage * Math.pow(1.1, combo));
	}

	public void addCombo(int comboValue) {
		setCombo(getCombo() + comboValue);
	}
	
	protected void applyKnockup(int knockup) {
		velocity.y -= knockup;
	}
	
	public Mage(MageType mageType){
		this(2000,2000, DEFAULT_WIDTH, DEFAULT_HEIGHT, mageType.color, "Training Bot");
		leftSpell = mageType.leftSpell;
		middleSpell = mageType.middleSpell;
		rightSpell = mageType.rightSpell;
	}


	public Mage(int x, int y, int width, int height, Color color, String name){
		setPosition(new Point2D.Double(x, y));
		setVelocity(new Point2D.Double(0, 0));
		setWidth(width);
		setHeight(height);
        setColor(color);
        setName(name);
		type = Constants.MAGE_TYPE;
	}
	
	public void setVelocityWithInput(UserInput input) {
		int x = 0, y = 0;
		if(isGrounded) {
			firstJump = true;
			secondJump = false;
		}
		switch (input.horizontalDirection) {
			case LEFT: x = -1; break;
            case NONE: x = 0; break;
			case RIGHT: x = 1; break;
		}
		switch (input.verticalDirection) {
			case JUMP:
				if(isGrounded) {
					y = 40;
				}
				else if(!isGrounded && !firstJump) {
					zeroVerticalVelocity();
					y = 40;
					secondJump = true;
					firstJump = true;
				}
			break;
			case NONE: 
			y = 0;
			if(!isGrounded && !secondJump) {
				firstJump = false;
			}
			break;

		}
		slowAmount += 0.02;
		if(slowAmount >= 1.0) {
			slowAmount = 1.0;
		}
		velocity.setLocation(velocity.getX() + x, velocity.getY() - y);
	}
	
	public boolean isAffectedByGravity(){
		return true;
	}

	public void decrementCooldown() {
		if(coolDown > 0){
			coolDown--;
		}
	}
	
	public int getCoolDown(){
		return coolDown;
	}

	public void setCoolDown(int i) {
		coolDown = i;
	}

	public void castSpell(UserInput input, GameState state) {
		if(getCoolDown() <= 0){
			Spell s = null;
			if (input.spell.equals(SpellInput.LEFT)){ 
				s = leftSpell;
			}
			else if (input.spell.equals(SpellInput.RIGHT)){ 
				s = rightSpell; 
			}
			else if (input.spell.equals(SpellInput.MIDDLE)) {
				s = middleSpell;
			}
			if (s != null) {
				Projectile projectile = s.createProjectile(this, input.mouseLocation, state);
				setCoolDown(s.getCoolDown());
				mana -= s.getManaCost();
				if (projectile != null) {
					state.entities.add(projectile);
				}
			}
		}
		else{
			decrementCooldown();
		}
	}


	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(getPosition().x*widthFactor);
		int y = (int)(getPosition().y*heightFactor);
		int scaledWidth = (int) (getWidth() * widthFactor);
		int scaledHeight = (int) (getHeight() * heightFactor);
		g.setColor(color);
		g.fillRect(x, y, scaledWidth, scaledHeight);
		g.setColor(Color.white);
		g.fillRect(x, y - 5, scaledWidth, 5);
		g.setColor(Color.green);
		g.fillRect(x, y - 25, scaledWidth * hp / MAX_HP, 10);
		writePlayerName(g, x, y);
		writeComboCounter(g, x, y);

	}

	private void writeComboCounter(Graphics g, int x, int y) {
		g.setColor(Color.BLACK);
		Font f = new Font("Verdana", Font.PLAIN, 12 + combo);
		g.setFont(f);
		g.drawString(Integer.toString(combo), x, y-40);
	}

	private void writePlayerName(Graphics g, int x, int y) {
		g.setColor(Color.BLACK);
		Font f = new Font("Verdana", Font.PLAIN, 12);
		g.setFont(f);
		g.drawString(name, x, y-30);
	}

	public void setName(String playerName) {
		name = playerName;
	}

	public int getCombo() {
		return combo;
	}

	public void setCombo(int combo) {
		this.combo = combo;
	}
	
	public void clearCombo() {
		combo = 0;
	}
}
