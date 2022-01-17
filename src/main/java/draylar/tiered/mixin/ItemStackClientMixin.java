package draylar.tiered.mixin;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin
{

    @Shadow
    public abstract NbtCompound getOrCreateSubNbt(String key);

    @Shadow
    public abstract boolean hasNbt();

    @Shadow
    public abstract NbtCompound getSubNbt(String key);

    private boolean isTiered = false;

    ///* private void storeAttributeModifier(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List> cir, List list, int integer, EquipmentSlot var6[], int var7, int var8, EquipmentSlot string, Multimap mutableText2, Iterator var11, Map$Entry entry, EntityAttributeModifier entityAttributeModifier) { */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getValue()D"), method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeAttributeModifier(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List> cir, List list, int integer, EquipmentSlot var6[], int var7, int var8, EquipmentSlot string, Multimap mutableText2, Iterator var11, Map.Entry<EntityAttribute, EntityAttributeModifier> entry, EntityAttributeModifier entityAttributeModifier)
    {
        isTiered = entityAttributeModifier.getName().contains("tiered:");
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TranslatableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 2), method = "getTooltip")
    private MutableText getFormatting(TranslatableText translatableText, Formatting formatting)
    {
        if (this.hasNbt() && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null && isTiered)
        {
            Identifier tier = new Identifier(
                    this.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            return translatableText.setStyle(attribute.getStyle());
        }
        else
        {
            return translatableText.formatted(formatting);
        }
    }

    @ModifyVariable(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"),
            index = 10
    )
    private Multimap<EntityAttribute, EntityAttributeModifier> sort(Multimap<EntityAttribute, EntityAttributeModifier> map)
    {
        Multimap<EntityAttribute, EntityAttributeModifier> vanillaFirst = LinkedListMultimap.create();
        Multimap<EntityAttribute, EntityAttributeModifier> remaining = LinkedListMultimap.create();

        map.forEach((entityAttribute, entityAttributeModifier) -> {
            if (!entityAttributeModifier.getName().contains("tiered"))
            {
                vanillaFirst.put(entityAttribute, entityAttributeModifier);
            }
            else
            {
                remaining.put(entityAttribute, entityAttributeModifier);
            }
        });

        vanillaFirst.putAll(remaining);
        return vanillaFirst;
    }

    @Inject(
            method = "getName",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyName(CallbackInfoReturnable<Text> cir)
    {
        if (this.hasNbt() && this.getSubNbt("display") == null && this.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null)
        {
            Identifier tier = new Identifier(
                    getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            // attempt to display attribute if it is valid
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            if (potentialAttribute != null)
            {
                cir.setReturnValue(new TranslatableText(potentialAttribute.getID() + ".label").append(" ")
                                                                                              .append(cir.getReturnValue())
                                                                                              .setStyle(
                                                                                                      potentialAttribute.getStyle()));
            }
        }
    }
}
