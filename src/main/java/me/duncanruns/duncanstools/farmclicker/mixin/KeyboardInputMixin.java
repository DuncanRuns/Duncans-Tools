package me.duncanruns.duncanstools.farmclicker.mixin;

import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void farmClicker_preventControls(CallbackInfo info) {
        if (FarmClicker.shouldPreventMovement()) {
            this.keyPresses = new Input(false, false, false, false, this.keyPresses.jump(), this.keyPresses.shift(), this.keyPresses.sprint());
            this.moveVector = new Vec2(0, 0);
            info.cancel();
        }
    }
}
