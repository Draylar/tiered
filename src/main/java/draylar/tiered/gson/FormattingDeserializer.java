package draylar.tiered.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public class FormattingDeserializer implements JsonDeserializer<Formatting> {

    @Override
    public Formatting deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Formatting.byName(json.getAsString().toUpperCase());
    }
}
