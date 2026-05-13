package me.duncanruns.duncanstools.farmclicker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.duncanruns.duncanstools.farmclicker.FarmClicker;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @WrapOperation(method = "onScroll",at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"))
    private void farmClicker_preventScroll(Inventory instance, int slot, Operation<Void> original){
        if (!FarmClicker.shouldPreventInteraction()) {
            original.call(instance, slot);
        }
    }
}
