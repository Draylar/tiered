package draylar.tiered.mixin;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Item.class)
public class ItemMixin {

    /**
     * The standard {@link Multimap} used for storing {@link EntityAttributeModifier}s in {@link Item} is not ordered. This mixin fixes it by changing the type to {@link LinkedListMultimap}, which is ordered.
     * <p>If the used {@link Multimap} is not ordered, attribute modifier tooltips will randomly change order every tick.
     *
     * @author Draylar
     * @param slot  {@link EquipmentSlot} to check for attribute modifiers in
     */
    @Overwrite
    public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot) {
        return LinkedListMultimap.create();
    }
}
