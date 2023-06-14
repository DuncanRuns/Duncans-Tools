package me.duncanruns.duncanstools.librarianbookhelper;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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

    public static boolean isWantedBook(ItemStack stack) {
        if (stack.getItem() != Items.ENCHANTED_BOOK) return false;
        NbtCompound enchant = EnchantedBookItem.getEnchantmentNbt(stack).getCompound(0);
        return (EnchantmentHelper.getIdFromNbt(enchant).equals(new Identifier(DuncansToolsConfig.getInstance().getLibrarianHighlight()))) &&
                (EnchantmentHelper.getLevelFromNbt(enchant) >= DuncansToolsConfig.getInstance().getLibrarianHighlightMinLevel());

    }
}
