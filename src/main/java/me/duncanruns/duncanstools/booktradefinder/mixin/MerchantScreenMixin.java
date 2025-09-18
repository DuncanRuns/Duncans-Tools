package me.duncanruns.duncanstools.booktradefinder.mixin;

import me.duncanruns.duncanstools.booktradefinder.BookTradeFinder;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin {
    @Inject(method = "renderMain", at = @At("HEAD"), cancellable = true)
    private void noScreenSpamWhenFinding(CallbackInfo info) {
        if (BookTradeFinder.moduleEnabled() && BookTradeFinder.finding) {
            info.cancel();
        }
    }
}
