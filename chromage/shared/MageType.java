package chromage.shared;

import chromage.shared.spells.*;

import java.awt.Color;

/**
 * Created by ahruss on 3/14/15.
 */
public enum MageType {
    ORANGE(new FruitPunch(), new Fireball(), new Lightning(), Color.ORANGE ),
    GREEN(new Iceball(), new Lifesteal(), new Lightning(), Color.GREEN ),
    PURPLE(new FruitPunch(), new Blink(), new Iceball(), Color.MAGENTA );
    
    public final Spell leftSpell;
    public final Spell middleSpell;
    public final Spell rightSpell;
    public final Color color;
    
    
    MageType(Spell left, Spell middle, Spell right, Color color) {
    	leftSpell = left;
    	middleSpell = middle;
    	rightSpell = right;
    	this.color = color;
    }
}
