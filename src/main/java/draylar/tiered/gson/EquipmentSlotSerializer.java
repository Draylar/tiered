package draylar.tiered.gson;

import com.google.gson.*;
import net.minecraft.entity.EquipmentSlot;

import java.lang.reflect.Type;

public class EquipmentSlotSerializer implements JsonSerializer<EquipmentSlot> {
	@Override
	public JsonElement serialize(EquipmentSlot src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.name());
	}
}
