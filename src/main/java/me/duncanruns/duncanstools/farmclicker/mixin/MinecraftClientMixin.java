package me.duncanruns.duncanstools.farmclicker.mixin;

import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    @Final
    public GameOptions options;

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventUse(CallbackInfo info) {
        if (FarmClicker.shouldPreventClickActions()) {
            info.cancel();
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventAttack(CallbackInfoReturnable<Boolean> info) {
        if (FarmClicker.shouldPreventClickActions()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventPick(CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            info.cancel();
        }
    }

    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventBreaking(boolean bl, CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            assert interactionManager != null;
            interactionManager.cancelBlockBreaking();
            info.cancel();
        }
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    @SuppressWarnings({"ControlFlowWithEmptyBody", "StatementWithEmptyBody"})
    private void farmClicker_preventHotbar(CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            while (Arrays.stream(options.hotbarKeys).anyMatch(KeyBinding::wasPressed)) ;
        }
    }
}
