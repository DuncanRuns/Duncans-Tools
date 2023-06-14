package me.duncanruns.duncanstools.craftrefill.mixin;

import me.duncanruns.duncanstools.craftrefill.CraftRefill;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("TAIL"))
    private void craftRefill_onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof InventoryScreen) {
            CraftRefill.gridSize = 4;
        } else if (screen instanceof CraftingScreen) {
            CraftRefill.gridSize = 9;
        }
    }
}
