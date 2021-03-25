package draylar.tiered.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow @Final private static TrackedData<Float> HEALTH;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Item attributes aren't applied until the player first ticks, which means any attributes
     *   such as bonus health are reset. This is annoying with health boosting armor.
     */
    @Redirect(
            method = "readCustomDataFromTag",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void trustOverflowHealth(LivingEntity livingEntity, float health) {
        this.dataTracker.set(HEALTH, health);
    }
}
