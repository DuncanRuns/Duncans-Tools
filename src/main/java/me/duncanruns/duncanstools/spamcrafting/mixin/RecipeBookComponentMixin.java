package me.duncanruns.duncanstools.spamcrafting.mixin;

import me.duncanruns.duncanstools.spamcrafting.mixinint.RecipeBookWidgetInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin implements RecipeBookWidgetInt {
    @Shadow
    private @Nullable RecipeCollection lastRecipeCollection;

    @Shadow
    private @Nullable RecipeDisplayId lastPlacedRecipe;

    @Shadow
    protected Minecraft minecraft;

    @Shadow
    @Final
    private GhostSlots ghostSlots;

    @Override
    public boolean duncanstools$reselect(boolean craftAll) {
        if (lastPlacedRecipe == null || this.lastRecipeCollection == null) return false;
        if (!this.lastRecipeCollection.isCraftable(lastPlacedRecipe)) return false;
        this.ghostSlots.clear();
        assert this.minecraft.gameMode != null;
        assert this.minecraft.player != null;
        this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, lastPlacedRecipe, craftAll);
        return true;
    }
}
