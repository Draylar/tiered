package draylar.tiered.mixin;

import com.mojang.authlib.GameProfile;
import draylar.tiered.Tiered;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity
{

    private DefaultedList<ItemStack> mainCopy = null;

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile)
    {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci)
    {
        // if main copy is null, set it to player inventory and check each stack
        if (mainCopy == null)
        {
            mainCopy = copyDefaultedList(getInventory().main);
            runCheck();
        }

        // if main copy =/= inventory, run check and set mainCopy to inventory
        if (!getInventory().main.equals(mainCopy))
        {
            mainCopy = copyDefaultedList(getInventory().main);
            runCheck();
        }
    }

    @Unique
    private DefaultedList<ItemStack> copyDefaultedList(DefaultedList<ItemStack> list)
    {
        DefaultedList<ItemStack> newList = DefaultedList.ofSize(36, ItemStack.EMPTY);

        for (int i = 0; i < list.size(); i++)
        {
            newList.set(i, list.get(i));
        }

        return newList;
    }

    @Unique
    private void runCheck()
    {
        getInventory().main.forEach(itemStack -> {
            // no tier on item
            if (itemStack.getSubNbt(Tiered.NBT_SUBTAG_KEY) == null)
            {
                // attempt to get a random tier
                Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(itemStack.getItem());

                // found an ID
                if (potentialAttributeID != null)
                {
                    itemStack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY)
                             .putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());
                }
            }
        });
    }
}
