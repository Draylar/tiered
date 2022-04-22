package draylar.tiered.mixin;

import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.tiered.Tiered;
import draylar.tiered.api.AttributeTemplate;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Mixin(Item.class)
public class ItemMixin {

    // onCraft in ItemStack class does get called too and get called in CraftingResultSlot
    // but is air at onTakeItem in CraftingResultSlot when quick crafting is used
    @Inject(method = "onCraft", at = @At("TAIL"))
    private void onCraftMixin(ItemStack stack, World world, PlayerEntity player, CallbackInfo info) {
        if (!world.isClient && stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) == null) {

            // attempt to get a random tier
            Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
            // found an ID
            if (potentialAttributeID != null) {
                stack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());

                HashMap<String, Object> nbtMap = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(potentialAttributeID.toString())).getNbtValues();

                // add durability nbt
                List<AttributeTemplate> attributeList = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(potentialAttributeID.toString())).getAttributes();
                for (int i = 0; i < attributeList.size(); i++)
                    if (attributeList.get(i).getAttributeTypeID().equals("tiered:generic.durable")) {
                        if (nbtMap == null)
                            nbtMap = new HashMap<String, Object>();
                        nbtMap.put("durable", attributeList.get(i).getEntityAttributeModifier().getValue());
                        break;
                    }
                // add nbtMap
                if (nbtMap != null) {
                    NbtCompound nbtCompound = stack.getNbt();
                    for (HashMap.Entry<String, Object> entry : nbtMap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        // json list will get read as ArrayList class
                        // json map will get read as linkedtreemap
                        // json integer is read by gson -> always double
                        if (value instanceof String)
                            nbtCompound.putString(key, (String) value);
                        else if (value instanceof Boolean)
                            nbtCompound.putBoolean(key, (boolean) value);
                        else if (value instanceof Double) {
                            if ((double) value % 1.0 < 0.0001D)
                                nbtCompound.putInt(key, (int) Math.round((double) value));
                            else
                                nbtCompound.putDouble(key, (double) value);
                        }
                    }
                    stack.setNbt(nbtCompound);
                }
            }
        }
    }

}
