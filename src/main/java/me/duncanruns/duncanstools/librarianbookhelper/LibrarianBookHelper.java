package me.duncanruns.duncanstools.librarianbookhelper;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class LibrarianBookHelper {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().librarianBookHelperEnabled;
    }

    public static boolean hasWantedBook(MerchantOffers list) {
        for (MerchantOffer tradeOffer : list) {
            if (isWantedBook(tradeOffer.getResult())) {
                return true;
            }
        }
        return false;
    }

    public static int getBookLevel(Holder<Enchantment> enchantment, ItemStack stack) {
        ItemEnchantments itemEnchantmentsComponent = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        return itemEnchantmentsComponent.getLevel(enchantment);
    }

    public static boolean isWantedBook(ItemStack stack) {
        if (stack.getItem() != Items.ENCHANTED_BOOK) return false;
        Holder<Enchantment> entry = EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().findFirst().orElseThrow();
        Identifier bookEnchantmentId = entry.unwrapKey().orElseThrow().identifier();
        int level = getBookLevel(entry, stack);

        Identifier highlightId = Identifier.parse(DuncansToolsConfig.getInstance().getLibrarianHighlight());
        int minLevel = DuncansToolsConfig.getInstance().getLibrarianHighlightMinLevel();
        return (bookEnchantmentId.equals(highlightId)) &&
                (level >= minLevel);

    }
}
