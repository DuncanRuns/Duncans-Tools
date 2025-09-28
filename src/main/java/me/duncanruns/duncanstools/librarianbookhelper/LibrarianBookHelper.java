package me.duncanruns.duncanstools.librarianbookhelper;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class LibrarianBookHelper {

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().librarianBookHelperEnabled;
    }

    public static boolean hasWantedBook(TradeOfferList list) {
        for (TradeOffer tradeOffer : list) {
            if (isWantedBook(tradeOffer.getSellItem())) {
                return true;
            }
        }
        return false;
    }

    public static int getBookLevel(RegistryEntry<Enchantment> enchantment, ItemStack stack) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        return itemEnchantmentsComponent.getLevel(enchantment);
    }

    public static boolean isWantedBook(ItemStack stack) {
        if (stack.getItem() != Items.ENCHANTED_BOOK) return false;
        RegistryEntry<Enchantment> entry = EnchantmentHelper.getEnchantments(stack).getEnchantments().stream().findFirst().orElseThrow();
        Identifier bookEnchantmentId = entry.getKey().orElseThrow().getValue();
        int level = getBookLevel(entry, stack);

        Identifier highlightId = Identifier.of(DuncansToolsConfig.getInstance().getLibrarianHighlight());
        int minLevel = DuncansToolsConfig.getInstance().getLibrarianHighlightMinLevel();
        return (bookEnchantmentId.equals(highlightId)) &&
                (level >= minLevel);

    }
}
