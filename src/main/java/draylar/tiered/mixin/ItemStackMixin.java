package draylar.tiered.mixin;

import com.google.common.collect.Multimap;
import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean hasTag();

    @Shadow private CompoundTag tag;

    @Shadow public abstract CompoundTag getOrCreateSubTag(String key);

    @Shadow public abstract CompoundTag getSubTag(String key);

    @Shadow public abstract Item getItem();

    @Inject(at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;values()Ljava/util/Collection;"), method = "getAttributeModifiers", locals = LocalCapture.CAPTURE_FAILHARD)
    private void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<String, EntityAttributeModifier>> cir, Multimap<String, EntityAttributeModifier> multimap) {
        if(this.getSubTag(Tiered.NBT_SUBTAG_KEY) != null) {
            Identifier tier = new Identifier(this.getOrCreateSubTag(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            if(!this.hasTag() || !this.tag.contains("AttributeModifiers", 9)) {
                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

                if(potentialAttribute != null) {
                    potentialAttribute.getAttributes().forEach(template -> {
                        // get required equipment slots
                        if(template.getRequiredEquipmentSlots() != null) {
                            List<EquipmentSlot> requiredEquipmentSlots = new ArrayList<>(Arrays.asList(template.getRequiredEquipmentSlots()));

                            if(requiredEquipmentSlots.contains(slot)) {
                                template.realize(multimap, slot);
                            }
                        }

                        // get optional equipment slots
                        if(template.getOptionalEquipmentSlots() != null) {
                            List<EquipmentSlot> optionalEquipmentSlots = new ArrayList<>(Arrays.asList(template.getOptionalEquipmentSlots()));

                            // optional equipment slots are valid ONLY IF the equipment slot is valid for the thing
                            if(optionalEquipmentSlots.contains(slot) && isPreferredEquipmentSlot(slot)) {
                                template.realize(multimap, slot);
                            }
                        }
                    });
                }
            }
        }
    }

    @Unique
    private boolean isPreferredEquipmentSlot(EquipmentSlot slot) {
        if(this.getItem() instanceof ArmorItem) {
            ArmorItem item = (ArmorItem) this.getItem();
            return item.getSlotType().equals(slot);
        }

        return slot == EquipmentSlot.MAINHAND;
    }
}
