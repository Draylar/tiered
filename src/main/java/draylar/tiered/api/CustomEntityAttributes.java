package draylar.tiered.api;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;

public class CustomEntityAttributes {

    public static final EntityAttribute DIG_SPEED = new ClampedEntityAttribute(null, "generic.digSpeed", 0.0D, 0.0D, 2048.0D).setTracked(true);
    public static final EntityAttribute CRIT_CHANCE = new ClampedEntityAttribute(null, "generic.critChance", 0.0D, 0.0D, 1D).setTracked(true);
//    public static final EntityAttribute DURABLE = new ClampedEntityAttribute(null, "generic.durable", 0.0D, 0.0D, 1D).setTracked(true);

    public static void init() {
        // NO-OP
    }
}
