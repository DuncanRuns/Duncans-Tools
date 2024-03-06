package me.duncanruns.duncanstools.craftrefill.mixin;

import me.duncanruns.duncanstools.craftrefill.CraftRefill;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "clickRecipe", at = @At("TAIL"))
    private void craftRefill_clickRecipeMixin(int syncId, RecipeEntry<?> recipe, boolean craftAll, CallbackInfo ci) {
        // Store recipe on click
        CraftRefill.storeRecipe(recipe);
    }
}
