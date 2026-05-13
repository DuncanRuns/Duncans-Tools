package me.duncanruns.duncanstools.alignmentlocker.mixin;

import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"), cancellable = true)
    private void alignmentLocker_overrideScroll(long handle, double xoffset, double yoffset, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (AlignmentLocker.lockKeyMapping.isDown()) {
            AlignmentLocker.scrollWithBind(yoffset > 0, player);
            info.cancel();
        }
    }
}
