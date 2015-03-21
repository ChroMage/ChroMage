package chromage.shared;

import chromage.shared.engine.*;
import chromage.shared.spells.*;
import chromage.shared.utils.Constants;
import chromage.shared.utils.UserInput;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Mage extends MobileEntity implements Serializable, Damagable, Comboable, Slowable {
    public static final int DEFAULT_HEIGHT = 300;
    public static final int DEFAULT_WIDTH = 100;
    public static final int MAX_HP = 1000;
    public static final int MAX_MANA = 300;
    public static final int MANA_REGEN = 2;
    static final long serialVersionUID = -50077493051991117L;
    public int hp = MAX_HP;
    public double mana = MAX_MANA;
    public Spell leftSpell = new Fireball();
    public Spell middleSpell = new Blink();
    public Spell rightSpell = new Iceball();
    public UserInput.SpellInput desiredSpell;
    public Point2D.Double target;
    public Type mageType;
    private int jumpsRemaining = 2;
    private double slowPercent = 1.0;
    private int coolDown = 0;
    private int combo;
    private String name = "AI";
    private boolean hasStoppedJumping = false;

    public Mage(Type mageType) {
        this(2000, 2000, DEFAULT_WIDTH, DEFAULT_HEIGHT, mageType.color, "Training Bot");
        leftSpell = mageType.leftSpell;
        middleSpell = mageType.middleSpell;
        rightSpell = mageType.rightSpell;
    }

    public Mage(int x, int y, int width, int height, Color color, String name) {
        setPosition(new Point2D.Double(x, y));
        setVelocity(new Point2D.Double(0, 0));
        setWidth(width);
        setHeight(height);
        setColor(color);
        setName(name);
        collisionBitMask = Constants.BLOCK_TYPE;
        categoryBitMask = Constants.MAGE_TYPE;
    }

    public Point2D.Double getVelocity() {
        return new Point2D.Double(super.getVelocity().x * slowPercent, super.getVelocity().y);
    }

    public boolean isDead() {
        return hp == 0;
    }

    public void takeDamage(int dmg) {
        hp -= getDamageWithCombo(dmg, combo);
        if (hp <= 0) {
            hp = 0;
            this.setShouldBeRemoved(true);
        }
    }

    public void slowBy(int slowAmount) {
        this.slowPercent *= (slowAmount / 100.0);
    }

    public void healDamage(int damage) {
        hp += damage;
        hp = Math.min(hp, MAX_HP);
    }

    public void applyFriction() {
        if (Math.abs(this.velocity.x) > .4) {
            this.velocity.x -= .5 * Math.signum(this.velocity.x);
        } else {
            this.velocity.x = 0;
        }
        int maxXVelocity = 15;
        if (this.velocity.x > maxXVelocity) {
            this.velocity.x = maxXVelocity;
        }
        if (this.velocity.x < -1 * maxXVelocity) {
            this.velocity.x = -1 * maxXVelocity;
        }
    }

    private int getDamageWithCombo(int damage, int combo) {
        return (int) (damage * Math.pow(1.1, combo));
    }

    public void addCombo(int comboValue) {
        setCombo(getCombo() + comboValue);
    }

    public void hitGround() {
        super.hitGround();
        jumpsRemaining = 2;
        clearCombo();
    }

    public void setVelocityWithInput(UserInput input) {
        double xAcceleration = 0, yAcceleration = 0;
        switch (input.horizontalDirection) {
            case LEFT:
                xAcceleration = -1;
                break;
            case NONE:
                xAcceleration = 0;
                break;
            case RIGHT:
                xAcceleration = 1;
                break;
        }
        switch (input.verticalDirection) {
            case JUMP:
                if (jumpsRemaining > 0 && hasStoppedJumping) {
                    hasStoppedJumping = false;
                    --jumpsRemaining;
                    yAcceleration = -velocity.y - 40;
                }
                break;
            case NONE:
                // if we just released the jump button and we're still moving up,
                // stop moving up so much. 
                if (!hasStoppedJumping && velocity.y <= 0) {
                    yAcceleration = -0.8*velocity.y;
                    hasStoppedJumping = true;
                }
                break;

        }
        slowPercent += 0.02;
        if (slowPercent >= 1.0) {
            slowPercent = 1.0;
        }
        velocity = Utilities.add(velocity, new Point2D.Double(xAcceleration, yAcceleration));
    }

    public boolean isAffectedByGravity() {
        return true;
    }

    public void decrementCooldown() {
        if (coolDown > 0) {
            coolDown--;
        }
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(int i) {
        coolDown = i;
    }

    public Spell getSpellForInput(UserInput.SpellInput input) {
        if (input == null) {
            return null;
        }
        switch (input) {
            case LEFT:
                return leftSpell;
            case RIGHT:
                return rightSpell;
            case MIDDLE:
                return middleSpell;
            default:
                return null;
        }
    }

    public boolean canCast(Spell spell) {
        return spell != null && getCoolDown() <= 0 && hasMana(spell.getManaCost());
    }

    public ArrayList<Projectile> castSpell(Collection<Entity> entities) {
        Spell s = getSpellForInput(getDesiredSpell());
        if (canCast(s)) {
            setCoolDown(s.getCoolDown());
            removeMana(s.getManaCost());
            return s.cast(this, target, entities);
        } else {
            decrementCooldown();
        }
        return new ArrayList<Projectile>();
    }

    public Collection<? extends Entity> update(Collection<Entity> entities) {
        Collection<Entity> created = new ArrayList<Entity>();
        created.addAll(super.update(entities));
        applyFriction();
        addMana(MANA_REGEN);
        created.addAll(castSpell(entities));
        return created;
    }

    public void draw(Graphics g, double heightFactor, double widthFactor) {
        int x = (int) (getPosition().x * widthFactor);
        int y = (int) (getPosition().y * heightFactor);
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

    public void acceptCollisionFrom(CollisionProcessor p) {
        p.processCollision((Damagable) this);
        p.processCollision((Comboable) this);
        p.processCollision((Slowable) this);
        p.processCollision((MobileEntity) this);
    }

    private void writeComboCounter(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        Font f = new Font("Verdana", Font.PLAIN, 12 + combo);
        g.setFont(f);
        g.drawString(Integer.toString(combo), x, y - 40);
    }

    private void writePlayerName(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        Font f = new Font("Verdana", Font.PLAIN, 12);
        g.setFont(f);
        g.drawString(name, x, y - 30);
    }

    public String getName() {
        return name;
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

    public double getSlowPercent() {
        return (slowPercent * 100);
    }

    public UserInput.SpellInput getDesiredSpell() {
        return desiredSpell;
    }

    public void setDesiredSpell(UserInput.SpellInput desiredSpell) {
        this.desiredSpell = desiredSpell;
    }

    public Point2D.Double getTarget() {
        return target;
    }

    public void setTarget(Point2D.Double target) {
        this.target = target;
    }

    public int getHealth() {
        return hp;
    }

    public static enum Type {
        ORANGE(new FruitPunch(), new Fireball(), new Lightning(), Color.ORANGE),
        GREEN(new Iceball(), new Lifesteal(), new Lightning(), Color.GREEN),
        PURPLE(new FruitPunch(), new Blink(), new Iceball(), Color.MAGENTA);

        public final Spell leftSpell;
        public final Spell middleSpell;
        public final Spell rightSpell;
        public final Color color;


        Type(Spell left, Spell middle, Spell right, Color color) {
            leftSpell = left;
            middleSpell = middle;
            rightSpell = right;
            this.color = color;
        }
    }
}
