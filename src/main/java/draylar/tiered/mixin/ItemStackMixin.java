package draylar.tiered.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract NbtCompound getNbt();

    @Shadow
    public abstract boolean hasNbt();

    @Inject(method = "getMaxDamage", at = @At("TAIL"), cancellable = true)
    private void getMaxDamageMixin(CallbackInfoReturnable<Integer> info) {
        if (hasNbt() && getNbt().contains("durable"))
            info.setReturnValue(info.getReturnValue() + (getNbt().getInt("durable") > 0 ? getNbt().getInt("durable") : (int) (getNbt().getFloat("durable") * info.getReturnValue())));
    }
}
