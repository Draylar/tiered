package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {

    public TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "onEntityHit", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/damage/DamageSource;trident(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;"), ordinal = 0)
    private float onEntityHitMixin(float original) {
        if (this.getOwner() instanceof ServerPlayerEntity) {
            EntityAttributeInstance instance = ((ServerPlayerEntity) this.getOwner()).getAttributeInstance(CustomEntityAttributes.RANGE_ATTACK_DAMAGE);
            if (instance != null) {
                float rangeDamage = original;
                for (EntityAttributeModifier modifier : instance.getModifiers()) {
                    float amount = (float) modifier.getValue();

                    if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION)
                        rangeDamage += amount;
                    else
                        rangeDamage *= (amount + 1);
                }
                return rangeDamage;
            }
        }
        return original;
    }
}
