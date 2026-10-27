package me.duncanruns.duncanstools.booktradefinder.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import me.duncanruns.duncanstools.booktradefinder.BookTradeFinder;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void onKeyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            BookTradeFinder.cancel = true;
        }
    }
}
