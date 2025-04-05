package me.duncanruns.duncanstools.farmclicker.mixin;

import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventControls(CallbackInfo info) {
        if (FarmClicker.shouldPreventMovement()) {
            this.playerInput = new PlayerInput(false, false, false, false, this.playerInput.jump(), this.playerInput.sneak(), this.playerInput.sprint());
            this.movementVector = new Vec2f(0,0);
            info.cancel();
        }
    }
}
