package draylar.tiered.gson;

import com.google.gson.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.lang.reflect.Type;

public class EntityAttributeModifierDeserializer implements JsonDeserializer<EntityAttributeModifier> {

    private static final String JSON_NAME_KEY = "name";
    private static final String JSON_AMOUNT_KEY = "amount";
    private static final String JSON_OPERATION_KEY = "operation";

    @Override
    public EntityAttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement name = getJsonElement(jsonObject, JSON_NAME_KEY, "Entity Attribute Modifier requires a name!");
        JsonElement amount = getJsonElement(jsonObject, JSON_AMOUNT_KEY, "Entity Attribute Modifier requires an amount!");
        JsonElement operation = getJsonElement(jsonObject, JSON_OPERATION_KEY, "Entity Attribute Modifier requires an operation!");

        return new EntityAttributeModifier(name.getAsString(), amount.getAsFloat(), EntityAttributeModifier.Operation.valueOf(operation.getAsString().toUpperCase()));
    }

    private JsonElement getJsonElement(JsonObject jsonObject, String jsonNameKey, String s) {
        JsonElement name;

        if (jsonObject.has(jsonNameKey))
            name = jsonObject.get(jsonNameKey);
        else
            throw new JsonParseException(s);

        return name;
    }
}
