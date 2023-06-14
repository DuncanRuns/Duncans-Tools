package me.duncanruns.duncanstools.craftrefill.mixin;

import me.duncanruns.duncanstools.craftrefill.mixinint.ClearSlotsMethodOwner;
import net.minecraft.client.gui.screen.recipebook.RecipeBookGhostSlots;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements ClearSlotsMethodOwner {
    @Shadow
    @Final
    protected RecipeBookGhostSlots ghostSlots;

    @Override
    public void clearSlots() {
        ghostSlots.reset();
    }
}
