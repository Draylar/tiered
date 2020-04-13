package draylar.tiered;

import draylar.tiered.api.CustomEntityAttributes;
import draylar.tiered.api.FabricArmorTags;
import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.data.AttributeDataLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Tiered implements ModInitializer {

    /**
     * Attribute Data Loader instance which handles loading attribute .json files from "data/modid/item_attributes".
     * <p> This field is registered to the server's data manager in {@link draylar.tiered.mixin.MinecraftServerMixin}
     */
    public static final AttributeDataLoader ATTRIBUTE_DATA_LOADER = new AttributeDataLoader();

    public static final UUID[] MODIFIERS = new UUID[] {
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"),
            UUID.fromString("4a88bc27-9563-4eeb-96d5-fe50917cc24f"),
            UUID.fromString("fee48d8c-1b51-4c46-9f4b-c58162623a7a")
    };

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String NBT_SUBTAG_KEY = "Tiered";
    public static final String NBT_SUBTAG_DATA_KEY = "Tier";

    @Override
    public void onInitialize() {
        FabricArmorTags.init();
        CustomEntityAttributes.init();

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            setupModifierLabel();
        }
    }

    /**
     * Returns an {@link Identifier} namespaced with this mod's modid ("tiered").
     *
     * @param path  path of identifier (eg. apple in "minecraft:apple")
     * @return  Identifier created with a namespace of this mod's modid ("tiered") and provided path
     */
    public static Identifier id(String path) {
        return new Identifier("tiered", path);
    }

    /**
     * Creates an {@link ItemTooltipCallback} listener that adds the modifier name at the top of an Item tooltip.
     * <p>A tool name is only displayed if the item has a modifier.
     */
    private void setupModifierLabel() {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, lines) -> {
            // has tier
            if(stack.getSubTag(NBT_SUBTAG_KEY) != null) {
                // get tier
                Identifier tier = new Identifier(stack.getOrCreateSubTag(NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

                // attempt to display attribute if it is valid
                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

                if(potentialAttribute != null) {
                    lines.add(1, new TranslatableText(potentialAttribute.getID() + ".label").setStyle(potentialAttribute.getStyle()));
                }
            }
        });
    }
}
