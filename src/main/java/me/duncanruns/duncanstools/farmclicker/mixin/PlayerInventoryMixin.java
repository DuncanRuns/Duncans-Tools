package me.duncanruns.duncanstools.farmclicker.mixin;

import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventScroll(double scrollAmount, CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            info.cancel();
        }
    }
}
