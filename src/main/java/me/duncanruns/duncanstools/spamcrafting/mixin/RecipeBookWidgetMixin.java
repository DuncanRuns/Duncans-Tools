package me.duncanruns.duncanstools.spamcrafting.mixin;

import me.duncanruns.duncanstools.spamcrafting.mixinint.RecipeBookWidgetInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements RecipeBookWidgetInt {
    @Shadow
    protected abstract boolean select(RecipeResultCollection results, NetworkRecipeId recipeId);

    @Shadow
    private @Nullable RecipeResultCollection selectedRecipeResults;

    @Shadow
    private @Nullable NetworkRecipeId selectedRecipeId;

    @Shadow
    protected MinecraftClient client;

    @Shadow
    @Final
    private GhostRecipe ghostRecipe;

    @Override
    public boolean duncanstools$reselect(boolean craftAll) {
        if (selectedRecipeId == null || this.selectedRecipeResults == null)
            return false;
        if (!this.selectedRecipeResults.isCraftable(selectedRecipeId) && selectedRecipeId.equals(this.selectedRecipeId))
            return false;
        this.ghostRecipe.clear();
        this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, selectedRecipeId, craftAll);
        return true;
    }
}
