package draylar.tiered.api;

import draylar.tiered.Tiered;
import draylar.tiered.util.SortList;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
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

    private ModifierUtils() {
        // no-op
    }
}