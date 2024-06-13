package me.duncanruns.duncanstools.librarianbookhelper.mixin;

import me.duncanruns.duncanstools.DuncansTools;
import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import me.duncanruns.duncanstools.librarianbookhelper.LibrarianBookHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {

    @Unique private boolean dinged = false;

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void librarianBookHelper_dingOnRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!LibrarianBookHelper.moduleEnabled()) return;

        if (LibrarianBookHelper.hasWantedBook(handler.getRecipes())) {
            playDingIfNeverDinged();
        }
    }

    // Not the most ideal place to mix into for this, but definitely the easiest since it gets given the trade offer and y value in the parameters.
    @Inject(method = "renderArrow", at = @At("HEAD"))
    private void librarianBookHelper_renderBookEnchant(DrawContext context, TradeOffer tradeOffer, int x, int y, CallbackInfo ci) {
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

        int textX = ((this.width - this.backgroundWidth) / 2) - textRenderer.getWidth(text);

        context.drawTooltip(textRenderer, text, textX - 16, y + 17);
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
