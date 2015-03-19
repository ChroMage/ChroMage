package chromage.shared;

import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.engine.Projectile;
import chromage.shared.spells.*;
import chromage.shared.utils.Constants;
import chromage.shared.utils.UserInput;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Mage extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

	//added
	private boolean secondJump = false;
	private boolean firstJump = false;
	private double slowAmount = 1.0;
	public static final int DEFAULT_HEIGHT = 300;
	public static final int DEFAULT_WIDTH = 100;
	public static final int MAX_HP = 1000;
	public static final int MAX_MANA = 300;
	public static final int MANA_REGEN = 2;
	public Point2D.Double getVelocity() {
		return new Point2D.Double(velocity.x*slowAmount, velocity.y*slowAmount);
	}
	private int coolDown = 0;
	public int hp = MAX_HP;
	public double mana = MAX_MANA;
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
		combo += comboValue;
		this.slowAmount = slowAmount/100.0;
		if (hp <= 0) {
			hp = 0;
			this.setShouldBeRemoved(true);
		}
	}

    @Override
    public void healDamage(int damage) {
        hp += damage;
        hp = Math.min(hp, MAX_HP);
    }

    @Override
    public void applyFriction() {
        if(Math.abs(this.velocity.x) > .4){
            this.velocity.x -= .5*Math.signum(this.velocity.x);
        }
        else{
            this.velocity.x = 0;
        }
        int maxXVelocity = 15;
        if(this.velocity.x > maxXVelocity){
            this.velocity.x = maxXVelocity;
        }
        if(this.velocity.x < -1*maxXVelocity){
            this.velocity.x = -1*maxXVelocity;
        }
    }

	private int getDamageWithCombo(int damage, int combo) {
		return (int) (damage * Math.pow(1.1, combo));
	}

	public void addCombo(int comboValue) {
		setCombo(getCombo() + comboValue);
	}
	
	public Mage(MageType mageType){
		this(2000,2000, DEFAULT_WIDTH, DEFAULT_HEIGHT, mageType.color, "Training Bot");
		leftSpell = mageType.leftSpell;
		middleSpell = mageType.middleSpell;
		rightSpell = mageType.rightSpell;
	}

    public void hitGround() {
        super.hitGround();
        clearCombo();
    }

	public Mage(int x, int y, int width, int height, Color color, String name){
		setPosition(new Point2D.Double(x, y));
		setVelocity(new Point2D.Double(0, 0));
		setWidth(width);
		setHeight(height);
        setColor(color);
        setName(name);
        collisionBitMask = Constants.BLOCK_TYPE;
        categoryBitMask = Constants.MAGE_TYPE;
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
		if(getCoolDown() <= 0 && (s != null) && hasMana(s.getManaCost())){
			Projectile projectile = s.createProjectile(this, input.mouseLocation, state);
			setCoolDown(s.getCoolDown());
			removeMana(s.getManaCost());
			if (projectile != null) {
				state.entities.add(projectile);
			}
		}
		else{
			decrementCooldown();
		}
	}

	public void update(ArrayList<Entity> entities){
		super.update(entities);
		addMana(MANA_REGEN);
	}

	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(getPosition().x*widthFactor);
		int y = (int)(getPosition().y*heightFactor);
		int scaledWidth = (int) (getWidth() * widthFactor);
		int scaledHeight = (int) (getHeight() * heightFactor);
		g.setColor(color);
		g.fillRect(x, y, scaledWidth, scaledHeight);
		g.setColor(Color.GREEN);
		g.fillRect(x, y - 25, scaledWidth * hp / MAX_HP, 10);
		g.setColor(Color.BLUE);
		g.fillRect(x, y - 15, (int) (scaledWidth * mana / MAX_MANA), 10);
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

    public String getName() { return name; }
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
	

	public double getMana() {
		return mana;
	}

	public void setMana(double mana) {
		this.mana = mana;
	}
	
	public void addMana(double mana) {
		this.mana = Math.min(mana + this.mana, MAX_MANA);
	}
	
	public void removeMana(double mana) {
		this.mana -= mana;
	}
	
	public boolean hasMana(double mana) {
		return this.mana >= mana;
	}
}
