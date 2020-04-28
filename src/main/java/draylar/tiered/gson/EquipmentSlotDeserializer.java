package draylar.tiered.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.entity.EquipmentSlot;

import java.lang.reflect.Type;

public class EquipmentSlotDeserializer implements JsonDeserializer<EquipmentSlot> {

    @Override
    public EquipmentSlot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return EquipmentSlot.byName(json.getAsString().toLowerCase());
    }
}
