package me.duncanruns.duncanstools.alignmentlocker.mixin;

import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void alignmentLocker_overrideScroll(double scrollAmount, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        if (player.getWorld().isClient && AlignmentLocker.lockKeyBinding.isPressed()) {
            AlignmentLocker.scrollWithBind(scrollAmount > 0, player);
            info.cancel();
        }
    }
}
