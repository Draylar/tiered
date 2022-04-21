package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowEntityMixin extends PersistentProjectileEntity {

    public SpectralArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "Lnet/minecraft/entity/projectile/SpectralArrowEntity;<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)V", at = @At("TAIL"))
    private void initMixin(World world, LivingEntity owner, CallbackInfo info) {
        if (this.getOwner() instanceof ServerPlayerEntity) {
            EntityAttributeInstance instance = ((ServerPlayerEntity) this.getOwner()).getAttributeInstance(CustomEntityAttributes.RANGE_ATTACK_DAMAGE);
            if (instance != null) {
                double rangeDamage = this.getDamage();
                for (EntityAttributeModifier modifier : instance.getModifiers()) {
                    float amount = (float) modifier.getValue();

                    if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION)
                        rangeDamage += amount;
                    else
                        rangeDamage *= (amount + 1);
                }
                this.setDamage(rangeDamage);
            }
        }
    }
}
