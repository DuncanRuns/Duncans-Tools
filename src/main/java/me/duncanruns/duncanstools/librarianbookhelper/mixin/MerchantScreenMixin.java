package me.duncanruns.duncanstools.librarianbookhelper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.librarianbookhelper.LibrarianBookHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Optional;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    @Shadow
    private int indexStartOffset;
    @Unique
    private boolean dinged = false;

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "renderMain", at = @At("TAIL"))
    private void librarianBookHelper_dingOnRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!LibrarianBookHelper.moduleEnabled()) return;

        if (LibrarianBookHelper.hasWantedBook(handler.getRecipes())) {
            playDingIfNeverDinged();
        }
    }

    @Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen;renderArrow(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/village/TradeOffer;II)V"))
    private void librarianBookHelper_renderBookEnchant(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci, @Local TradeOfferList tradeOffers, @Local TradeOffer tradeOffer) {
        if (!LibrarianBookHelper.moduleEnabled()) return;

        ItemStack sellItem = tradeOffer.getSellItem();
        if (sellItem.getItem() != Items.ENCHANTED_BOOK) {
            return;
        }

        Optional<RegistryEntry<Enchantment>> enchantmentOpt = EnchantmentHelper.getEnchantments(sellItem).getEnchantments().stream().findFirst();
        if (enchantmentOpt.isEmpty()) {
            return;
        }

        MutableText text = Text.empty().append(Enchantment.getName(enchantmentOpt.get(), LibrarianBookHelper.getBookLevel(enchantmentOpt.get(), sellItem)).getString());

        if (LibrarianBookHelper.isWantedBook(sellItem)) {
            text.styled(style -> style.withColor(Formatting.GREEN).withBold(true));
        }

        int i = tradeOffers.indexOf(tradeOffer);
        int y = (this.height - this.backgroundHeight) / 2 + 39 + (20 * (i - this.indexStartOffset - 1));

        int textX = ((this.width - this.backgroundWidth) / 2) - textRenderer.getWidth(text);

        context.drawTooltipImmediately(textRenderer, Collections.singletonList(new OrderedTextTooltipComponent(text.asOrderedText())), textX - 16, y + 17, HoveredTooltipPositioner.INSTANCE, null);
    }

    @Unique
    private void playDingIfNeverDinged() {
        if (dinged) return;
        dinged = true;
        if (DuncansToolsConfig.getInstance().librarianHighlightDing) {
            DuncansTools.ding(client);
        }
    }

}
