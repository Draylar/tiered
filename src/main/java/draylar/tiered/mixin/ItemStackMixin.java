package draylar.tiered.mixin;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract NbtCompound getOrCreateSubNbt(String key);

    @Shadow
    public abstract NbtCompound getNbt();

    @Shadow
    public abstract boolean hasNbt();

    @Shadow
    public abstract NbtCompound getSubNbt(String key);

    @Redirect(method = "getAttributeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiersMixin(Item item, EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> mods = item.getAttributeModifiers(slot);
        Multimap<EntityAttribute, EntityAttributeModifier> newMap = LinkedListMultimap.create();
        newMap.putAll(mods);

        if (getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            if (!hasNbt() || !getNbt().contains("AttributeModifiers", 9)) {
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
                            if (optionalEquipmentSlots.contains(slot) && Tiered.isPreferredEquipmentSlot((ItemStack) (Object) this, slot))
                                template.realize(newMap, slot);
                        }
                    });
                }
            }
        }

        return newMap;
    }

    @Inject(method = "getMaxDamage", at = @At("TAIL"), cancellable = true)
    private void getMaxDamageMixin(CallbackInfoReturnable<Integer> info) {
        if (hasNbt() && getNbt().contains("durable"))
            info.setReturnValue(info.getReturnValue() + (getNbt().getInt("durable") > 0 ? getNbt().getInt("durable") : (int) (getNbt().getFloat("durable") * info.getReturnValue())));
    }
}
