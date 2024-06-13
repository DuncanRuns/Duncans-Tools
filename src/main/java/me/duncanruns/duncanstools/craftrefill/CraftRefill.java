package me.duncanruns.duncanstools.craftrefill;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.recipe.RecipeEntry;

public class CraftRefill {
    // Should replace the following 3 fields with Map<Class<? extends Screen>,Recipe<?>>
    public static int gridSize = 4;
    public static RecipeEntry<?> lastRecipe4 = null;
    public static RecipeEntry<?> lastRecipe9 = null;
    public static StonecuttingRecipeInfo lastSCRecipe = null;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().craftRefillEnabled;
    }

    public static void storeRecipe(RecipeEntry<?> recipe) {
        if (gridSize == 4) {
            lastRecipe4 = recipe;
        } else {
            lastRecipe9 = recipe;
        }
    }

    public static boolean recipeExists() {
        return getRecipe() != null;
    }

    public static RecipeEntry<?> getRecipe() {
        return gridSize == 4 ? lastRecipe4 : lastRecipe9;
    }
}
