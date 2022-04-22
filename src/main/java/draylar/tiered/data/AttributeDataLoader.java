package draylar.tiered.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.gson.EntityAttributeModifierDeserializer;
import draylar.tiered.gson.EntityAttributeModifierSerializer;
import draylar.tiered.gson.EquipmentSlotDeserializer;
import draylar.tiered.gson.EquipmentSlotSerializer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AttributeDataLoader extends JsonDataLoader implements SimpleSynchronousResourceReloadListener {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().registerTypeAdapter(EntityAttributeModifier.class, new EntityAttributeModifierDeserializer())
            .registerTypeAdapter(EntityAttributeModifier.class, new EntityAttributeModifierSerializer()).registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotSerializer())
            .registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotDeserializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).create();

    private static final String PARSING_ERROR_MESSAGE = "Parsing error loading recipe {}";
    private static final String LOADED_RECIPES_MESSAGE = "Loaded {} recipes";
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<Identifier, PotentialAttribute> itemAttributes = new HashMap<>();

    public AttributeDataLoader() {
        super(GSON, "item_attributes");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        Map<Identifier, PotentialAttribute> readItemAttributes = Maps.newHashMap();

        for (Map.Entry<Identifier, JsonElement> entry : loader.entrySet()) {
            Identifier identifier = entry.getKey();

            try {
                PotentialAttribute itemAttribute = GSON.fromJson(entry.getValue(), PotentialAttribute.class);
                readItemAttributes.put(new Identifier(itemAttribute.getID()), itemAttribute);
            } catch (IllegalArgumentException | JsonParseException exception) {
                LOGGER.error(PARSING_ERROR_MESSAGE, identifier, exception);
            }
        }

        itemAttributes = readItemAttributes;
        LOGGER.info(LOADED_RECIPES_MESSAGE, readItemAttributes.size());
    }

    public Map<Identifier, PotentialAttribute> getItemAttributes() {
        return itemAttributes;
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("tiered", "item_attributes");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
    }

}
