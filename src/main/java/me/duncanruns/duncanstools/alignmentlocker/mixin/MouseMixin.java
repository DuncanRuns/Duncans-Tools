package me.duncanruns.duncanstools.alignmentlocker.mixin;

import me.duncanruns.duncanstools.alignmentlocker.AlignmentLocker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"), cancellable = true)
    private void alignmentLocker_overrideScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        if (!AlignmentLocker.moduleEnabled()) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (AlignmentLocker.lockKeyBinding.isPressed()) {
            AlignmentLocker.scrollWithBind(vertical > 0, player);
            info.cancel();
        }
    }
}
