package draylar.tiered.mixin;

import draylar.tiered.Tiered;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourceManager.class)
public class ServerResourceManagerMixin
{

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci)
    {
        this.resourceManager.registerReloader(Tiered.ATTRIBUTE_DATA_LOADER);
    }
}
