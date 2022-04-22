package draylar.tiered.api;

import draylar.tiered.Tiered;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemVerifier {

    private final String id;
    private final String tag;

    public ItemVerifier(String id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    /**
     * Returns whether the given {@link Identifier} is valid for this ItemVerifier, which may check direct against either a {@link Identifier} or {@link Tag<Item>}.
     * <p>
     * The given {@link Identifier} should be the ID of an {@link Item} in {@link Registry#ITEM}.
     *
     * @param itemID item registry ID to check against this verifier
     * @return whether the check succeeded
     */
    public boolean isValid(Identifier itemID) {
        return isValid(itemID.toString());
    }

    /**
     * Returns whether the given {@link String} is valid for this ItemVerifier, which may check direct against either a {@link Identifier} or {@link Tag<Item>}.
     * <p>
     * The given {@link String} should be the ID of an {@link Item} in {@link Registry#ITEM}.
     *
     * @param itemID item registry ID to check against this verifier
     * @return whether the check succeeded
     */
    public boolean isValid(String itemID) {
        if (id != null) {
            return itemID.equals(id);
        } else if (tag != null) {
            TagKey<Item> itemTag = TagKey.of(Registry.ITEM_KEY, new Identifier(tag));
            // TagKey<Item> itemTag = ItemTags.getTagGroup().getTag(new Identifier(tag));

            if (itemTag != null) {
                return new ItemStack(Registry.ITEM.get(new Identifier(itemID))).isIn(itemTag);// itemTag.contains(Registry.ITEM.get(new Identifier(itemID)));
            } else {
                Tiered.LOGGER.error(tag + " was specified as an item verifier tag, but it does not exist!");
            }
        }

        return false;
    }
}
