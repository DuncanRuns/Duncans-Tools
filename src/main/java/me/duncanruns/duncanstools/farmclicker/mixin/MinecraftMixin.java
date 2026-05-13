package me.duncanruns.duncanstools.farmclicker.mixin;

import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.Options;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;

    @Shadow
    @Final
    public Options options;

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventUse(CallbackInfo info) {
        if (FarmClicker.shouldPreventClickActions()) {
            info.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventAttack(CallbackInfoReturnable<Boolean> info) {
        if (FarmClicker.shouldPreventClickActions()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventPick(CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            info.cancel();
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventBreaking(boolean down, CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            assert gameMode != null;
            gameMode.stopDestroyBlock();
            info.cancel();
        }
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    @SuppressWarnings({"ControlFlowWithEmptyBody", "StatementWithEmptyBody"})
    private void farmClicker_preventHotbar(CallbackInfo info) {
        if (FarmClicker.shouldPreventInteraction()) {
            while (Arrays.stream(options.keyHotbarSlots).anyMatch(KeyMapping::isDown)) ;
        }
    }
}
