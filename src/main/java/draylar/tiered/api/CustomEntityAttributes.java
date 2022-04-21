package draylar.tiered.api;

import draylar.tiered.Tiered;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.registry.Registry;

public class CustomEntityAttributes {

    public static final EntityAttribute DIG_SPEED = register(new ClampedEntityAttribute("generic.dig_speed", 0.0D, 0.0D, 2048.0D).setTracked(true));
    public static final EntityAttribute CRIT_CHANCE = register(new ClampedEntityAttribute("generic.crit_chance", 0.0D, 0.0D, 1D).setTracked(true));
    public static final EntityAttribute DURABLE = register(new ClampedEntityAttribute("generic.durable", 0.0D, 0.0D, 1D).setTracked(true));
    public static final EntityAttribute RANGE_ATTACK_DAMAGE = register(new ClampedEntityAttribute("generic.range_attack_damage", 0.0D, 0.0D, 2048.0D).setTracked(true));

    public static void init() {
        // NO-OP
    }

    private static EntityAttribute register(EntityAttribute attribute) {
        return Registry.register(Registry.ATTRIBUTE, Tiered.id(attribute.getTranslationKey()), attribute);
    }
}
