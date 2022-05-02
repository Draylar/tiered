package draylar.tiered.api;

import draylar.tiered.Tiered;
import draylar.tiered.util.SortList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

public class ModifierUtils {

    /**
     * Returns the ID of a random attribute that is valid for the given {@link Item} in {@link Identifier} form.
     * <p>
     * If there is no valid attribute for the given {@link Item}, null is returned.
     *
     * @param item {@link Item} to generate a random attribute for
     * @return id of random attribute for item in {@link Identifier} form, or null if there are no valid options
     */
    @Nullable
    public static Identifier getRandomAttributeIDFor(Item item) {
        List<Identifier> potentialAttributes = new ArrayList<>();
        List<Integer> attributeWeights = new ArrayList<>();
        // collect all valid attributes for the given item and their weights

        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if (attribute.isValid(Registry.ITEM.getId(item)) && attribute.getWeight() > 0) {
                potentialAttributes.add(new Identifier(attribute.getID()));
                attributeWeights.add(attribute.getWeight());
            }
        });

        // return a random attribute if there are any, or null if there are none
        if (potentialAttributes.size() > 0) {
            int totalWeight = 0;
            for (Integer weight : attributeWeights)
                totalWeight += weight.intValue();
            int randomChoice = new Random().nextInt(totalWeight);
            SortList.concurrentSort(attributeWeights, attributeWeights, potentialAttributes);

            for (int i = 0; i < attributeWeights.size(); i++) {
                if (randomChoice < attributeWeights.get(i))
                    return potentialAttributes.get(i);
                randomChoice -= attributeWeights.get(i);
            }
            // If random choice didn't work
            return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
        } else
            return null;
    }

    // Set on
    public static void setItemStackAttribute(ItemStack stack) {
        if (stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) == null) {

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

    private ModifierUtils() {
        // no-op
    }
}