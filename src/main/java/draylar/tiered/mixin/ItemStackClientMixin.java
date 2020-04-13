package draylar.tiered.mixin;

import com.google.common.collect.Multimap;
import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow public abstract CompoundTag getOrCreateSubTag(String key);

    private boolean isTiered = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getAmount()D"), method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeAttributeModifier(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List> cir, List list, int i, EquipmentSlot var6[], int var7, int var8, EquipmentSlot equipmentSlot, Multimap multimap, Iterator var11, Map.Entry entry, EntityAttributeModifier entityAttributeModifier) {
        isTiered = entityAttributeModifier.getName().contains("tiered:");
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Text;", ordinal = 2), method = "getTooltip")
    private Text getFormatting(TranslatableText texts, Formatting formatting) {
        if(isTiered) {
            Identifier tier = new Identifier(this.getOrCreateSubTag(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            return texts.setStyle(attribute.getStyle());
        } else {
            return texts.formatted(formatting);
        }
    }
}
