package me.duncanruns.duncanstools.spamcrafting.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import me.duncanruns.duncanstools.spamcrafting.SpamCrafting;
import me.duncanruns.duncanstools.spamcrafting.mixinint.RecipeBookWidgetInt;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin<T extends RecipeBookMenu>
        extends AbstractContainerScreen<T>
        implements RecipeUpdateListener {

    @Shadow
    @Final
    private RecipeBookComponent<?> recipeBookComponent;

    public AbstractRecipeBookScreenMixin(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void checkSpamCraft(CallbackInfo ci) {
        if (SpamCrafting.moduleEnabled() && InputConstants.isKeyDown(minecraft.getWindow(), SpamCrafting.keyMapping.key.getValue())) {
            // Maximum amount that can be crafted in a tick is a full inventory of an item for a single item recipe plus a stack of it already in the grid = 37 stacks.
            assert this.minecraft.gameMode != null;
            for (int i = 0; i < 37 && ((RecipeBookWidgetInt) this.recipeBookComponent).duncanstools$reselect(true); i++) {
                assert this.minecraft.player != null;
                this.minecraft.gameMode.handleContainerInput(this.menu.containerId, 0, 1, ContainerInput.THROW, this.minecraft.player);
            }
        }
    }
}
