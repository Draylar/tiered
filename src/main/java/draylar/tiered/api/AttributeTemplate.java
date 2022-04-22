package draylar.tiered.api;

import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;
import draylar.tiered.Tiered;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Stores information on an AttributeModifier template applied to an ItemStack.
 *
 * The ID of the AttributeTemplate is the logical ID used to determine what "type" of attribute of is. An EntityAttributeModifier has: - a UUID, which is a unique identifier to separate different
 * attributes of the same type - a name, which is used for generating a non-specified UUID and displaying in tooltips in some context - an amount, which is used in combination with the operation to
 * modify the final relevant value - a modifier, which can be something such as addition or subtraction
 *
 * The EquipmentSlot is used to only apply this template to certain items.
 */
public class AttributeTemplate {

    @SerializedName("type")
    private final String attributeTypeID;

    @SerializedName("modifier")
    private final EntityAttributeModifier entityAttributeModifier;

    @SerializedName("required_equipment_slots")
    private final EquipmentSlot[] requiredEquipmentSlots;

    @SerializedName("optional_equipment_slots")
    private final EquipmentSlot[] optionalEquipmentSlots;

    public AttributeTemplate(String attributeTypeID, EntityAttributeModifier entityAttributeModifier, EquipmentSlot[] requiredEquipmentSlots, EquipmentSlot[] optionalEquipmentSlots) {
        this.attributeTypeID = attributeTypeID;
        this.entityAttributeModifier = entityAttributeModifier;
        this.requiredEquipmentSlots = requiredEquipmentSlots;
        this.optionalEquipmentSlots = optionalEquipmentSlots;
    }

    public EquipmentSlot[] getRequiredEquipmentSlots() {
        return requiredEquipmentSlots;
    }

    public EquipmentSlot[] getOptionalEquipmentSlots() {
        return optionalEquipmentSlots;
    }

    public EntityAttributeModifier getEntityAttributeModifier() {
        return entityAttributeModifier;
    }

    public String getAttributeTypeID() {
        return attributeTypeID;
    }

    /**
     * Uses this {@link AttributeTemplate} to create an {@link EntityAttributeModifier}, which is placed into the given {@link Multimap}.
     * <p>
     * Note that this method assumes the given {@link Multimap} is mutable.
     *
     * @param multimap map to add {@link AttributeTemplate}
     * @param slot
     */
    public void realize(Multimap<EntityAttribute, EntityAttributeModifier> multimap, EquipmentSlot slot) {
        EntityAttributeModifier cloneModifier = new EntityAttributeModifier(Tiered.MODIFIERS[slot.getArmorStandSlotId()], entityAttributeModifier.getName() + "_" + slot.getName(),
                entityAttributeModifier.getValue(), entityAttributeModifier.getOperation());

        EntityAttribute key = Registry.ATTRIBUTE.get(new Identifier(attributeTypeID));
        if (key == null)
            Tiered.LOGGER.warn(String.format("%s was referenced as an attribute type, but it does not exist! A data file in /tiered/item_attributes/ has an invalid type property.", attributeTypeID));
        else
            multimap.put(key, cloneModifier);
    }
}
