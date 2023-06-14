package me.duncanruns.duncanstools.craftrefill;

import me.duncanruns.duncanstools.config.DuncansToolsConfig;
import net.minecraft.recipe.Recipe;

public class CraftRefill {
    // Should replace the following 3 fields with Map<Class<? extends Screen>,Recipe<?>>
    public static int gridSize = 4;
    public static Recipe<?> lastRecipe4 = null;
    public static Recipe<?> lastRecipe9 = null;
    public static StonecuttingRecipeInfo lastSCRecipe = null;

    public static boolean moduleEnabled() {
        return DuncansToolsConfig.getInstance().craftRefillEnabled;
    }

    public static void storeRecipe(Recipe<?> recipe) {
        if (gridSize == 4) {
            lastRecipe4 = recipe;
        } else {
            lastRecipe9 = recipe;
        }
    }

    public static boolean recipeExists() {
        return getRecipe() != null;
    }

    public static Recipe<?> getRecipe() {
        return gridSize == 4 ? lastRecipe4 : lastRecipe9;
    }
}
