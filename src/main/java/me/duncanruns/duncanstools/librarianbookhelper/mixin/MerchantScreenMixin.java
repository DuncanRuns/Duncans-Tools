package me.duncanruns.duncanstools.librarianbookhelper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.librarianbookhelper.LibrarianBookHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Optional;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {

    @Shadow
    private int scrollOff;
    @Unique
    private boolean dinged = false;

    public MerchantScreenMixin(MerchantMenu handler, Inventory inventory, MutableComponent title) {
        super(handler, inventory, title);
    }

    @Inject(method = "extractContents", at = @At("TAIL"))
    private void librarianBookHelper_dingOnRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!LibrarianBookHelper.moduleEnabled()) return;

        if (LibrarianBookHelper.hasWantedBook(menu.getOffers())) {
            playDingIfNeverDinged();
        }
    }

    @Inject(method = "extractContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;extractButtonArrows(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/item/trading/MerchantOffer;II)V"))
    private void librarianBookHelper_renderBookEnchant(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci, @Local MerchantOffers offers, @Local MerchantOffer offer) {
        if (!LibrarianBookHelper.moduleEnabled()) return;

        ItemStack sellItem = offer.getResult();
        if (sellItem.getItem() != Items.ENCHANTED_BOOK) {
            return;
        }

        Optional<Holder<Enchantment>> enchantmentOpt = EnchantmentHelper.getEnchantmentsForCrafting(sellItem).keySet().stream().findFirst();
        if (enchantmentOpt.isEmpty()) {
            return;
        }

        MutableComponent text = Component.empty().append(Enchantment.getFullname(enchantmentOpt.get(), LibrarianBookHelper.getBookLevel(enchantmentOpt.get(), sellItem)).getString());

        if (LibrarianBookHelper.isWantedBook(sellItem)) {
            text.withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true));
        }

        int i = offers.indexOf(offer);
        int y = (this.height - this.imageHeight) / 2 + 39 + (20 * (i - this.scrollOff - 1));

        int textX = ((this.width - this.imageWidth) / 2) - font.width(text);

        graphics.tooltip(font, Collections.singletonList(new ClientTextTooltip(text.getVisualOrderText())), textX - 16, y + 17, DefaultTooltipPositioner.INSTANCE, null);
    }

    @Unique
    private void playDingIfNeverDinged() {
        if (dinged) return;
        dinged = true;
        if (DuncansToolsConfig.getInstance().librarianHighlightDing) {
            DuncansTools.ding(minecraft);
        }
    }

}
