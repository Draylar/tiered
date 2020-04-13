package draylar.tiered.api;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FabricArmorTags {

    public static final Tag<Item> HELMETS = register("helmets");
    public static final Tag<Item> CHESTPLATES = register("chestplates");
    public static final Tag<Item> LEGGINGS = register("leggings");
    public static final Tag<Item> BOOTS = register("boots");

    private FabricArmorTags() { }

    public static void init() {

    }

    private static Tag<Item> register(String id) {
        return TagRegistry.item(new Identifier("fabric", id));
    }
}
