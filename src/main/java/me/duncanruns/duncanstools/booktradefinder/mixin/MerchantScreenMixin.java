package me.duncanruns.duncanstools.booktradefinder.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.duncanstools.booktradefinder.BookTradeFinder;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {
    public MerchantScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "extractContents", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings("LocalMayUseName")
    private void noScreenSpamWhenFinding(CallbackInfo info, @Local(argsOnly = true) final GuiGraphicsExtractor graphics) {
        if (!(BookTradeFinder.moduleEnabled() && BookTradeFinder.finding)) {
            return;
        }
        info.cancel();
        List<String> displayText = BookTradeFinder.getDisplayText();
        if (displayText.isEmpty()) return;
        int displayTextWidth = displayText.stream().mapToInt(font::width).max().orElse(0);
        int displayTextHeight = font.lineHeight * displayText.size();
        List<ClientTooltipComponent> tooltipList = displayText.stream()
                .map(Component::literal)
                .map(MutableComponent::getVisualOrderText)
                .map(ClientTextTooltip::new)
                .map(t -> (ClientTooltipComponent) t)
                .toList();
        graphics.tooltip(font, tooltipList, (this.width - displayTextWidth) / 2, (height - displayTextHeight) / 2, DefaultTooltipPositioner.INSTANCE, null);
    }
}
