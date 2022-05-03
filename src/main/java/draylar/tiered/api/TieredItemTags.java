package draylar.tiered.api;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

//Since v1.0.3 unused cause of AutoTag usage
public class TieredItemTags {

    // public static final TagKey<Item> HELMETS = register("helmets");
    // public static final TagKey<Item> CHESTPLATES = register("chestplates");
    // public static final TagKey<Item> LEGGINGS = register("leggings");
    // public static final TagKey<Item> BOOTS = register("boots");
    // public static final TagKey<Item> SHIELDS = register("shields");
    // public static final TagKey<Item> SWORDS = register("swords");
    // public static final TagKey<Item> SHOVELS = register("shovels");
    // public static final TagKey<Item> PICKAXES = register("pickaxes");
    // public static final TagKey<Item> HOES = register("hoes");
    // public static final TagKey<Item> AXES = register("axes");

    private TieredItemTags() {
    }

    public static void init() {
    }

    private static TagKey<Item> register(String id) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier("fabric", id));
    }
}
