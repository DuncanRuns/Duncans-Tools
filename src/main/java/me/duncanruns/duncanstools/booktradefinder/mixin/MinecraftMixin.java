package me.duncanruns.duncanstools.booktradefinder.mixin;

import me.duncanruns.duncanstools.booktradefinder.BookTradeFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof MerchantScreen merchantScreen) {
            BookTradeFinder.onOpenMerchantScreen(merchantScreen);
        }
    }
}
