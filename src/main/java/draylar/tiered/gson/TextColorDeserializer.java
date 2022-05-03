package draylar.tiered.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

// Unused cause of FormattingDeserializer usage
public class TextColorDeserializer implements JsonDeserializer<TextColor> {

    @Override
    public TextColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return TextColor.fromFormatting(Formatting.byName(json.getAsString().toUpperCase()));
    }
}
