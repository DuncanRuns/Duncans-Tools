package me.duncanruns.duncanstools.craftrefill;

import net.minecraft.item.Item;

public record StonecuttingRecipeInfo(Item inputItem, int buttonId, Item outputItem) {
    @Override
    public String toString() {
        return "StonecuttingRecipeInfo{" +
                "inputItem=" + inputItem +
                ", buttonId=" + buttonId +
                ", outputItem=" + outputItem +
                '}';
    }
}
