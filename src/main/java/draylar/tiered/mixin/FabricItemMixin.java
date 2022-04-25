package draylar.tiered.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Mixin(FabricItem.class)
public interface FabricItemMixin {

    @Inject(method = "getAttributeModifiers", at = @At("HEAD"), cancellable = true)
    default void getAttributeModifiers(ItemStack stack, EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> info) {
        Multimap<EntityAttribute, EntityAttributeModifier> mods = stack.getItem().getAttributeModifiers(slot);
        Multimap<EntityAttribute, EntityAttributeModifier> newMap = LinkedListMultimap.create();
        newMap.putAll(mods);

        if (stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(stack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            if (!stack.hasNbt() || !stack.getNbt().contains("AttributeModifiers", 9)) {
                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

                if (potentialAttribute != null) {
                    potentialAttribute.getAttributes().forEach(template -> {
                        // get required equipment slots
                        if (template.getRequiredEquipmentSlots() != null) {
                            List<EquipmentSlot> requiredEquipmentSlots = new ArrayList<>(Arrays.asList(template.getRequiredEquipmentSlots()));

                            if (requiredEquipmentSlots.contains(slot))
                                template.realize(newMap, slot);
                        }

                        // get optional equipment slots
                        if (template.getOptionalEquipmentSlots() != null) {
                            List<EquipmentSlot> optionalEquipmentSlots = new ArrayList<>(Arrays.asList(template.getOptionalEquipmentSlots()));

                            // optional equipment slots are valid ONLY IF the equipment slot is valid for the thing
                            if (optionalEquipmentSlots.contains(slot) && Tiered.isPreferredEquipmentSlot(stack, slot))
                                template.realize(newMap, slot);
                        }
                    });
                }
            }
        }

        info.setReturnValue(newMap);
    }
}
