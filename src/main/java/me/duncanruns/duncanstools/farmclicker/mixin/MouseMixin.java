package me.duncanruns.duncanstools.farmclicker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @WrapOperation(method = "onMouseScroll",at= @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"))
    private void farmClicker_preventScroll(PlayerInventory instance, int slot, Operation<Void> original){
        if (!FarmClicker.shouldPreventInteraction()) {
            original.call(instance, slot);
        }
    }
}
