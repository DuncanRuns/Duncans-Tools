package me.duncanruns.duncanstools.farmclicker.mixin;

import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void farmClicker_changeLookDirectionStartMixin(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        if (FarmClicker.shouldPreventMovement() && isClientPlayer()) {
            info.cancel();
        }
    }

    @Unique
    private boolean isClientPlayer() { // Prevents if resolving to always false
        return ((Object) this) instanceof ClientPlayerEntity;
    }
}
