package me.duncanruns.duncanstools.spamcrafting.mixin;

import me.duncanruns.duncanstools.spamcrafting.SpamCrafting;
import me.duncanruns.duncanstools.spamcrafting.mixinint.RecipeBookWidgetInt;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin<T extends AbstractRecipeScreenHandler>
        extends HandledScreen<T>
        implements RecipeBookProvider {

    @Shadow
    @Final
    private RecipeBookWidget<?> recipeBook;

    public RecipeBookScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Inject(method = "handledScreenTick", at = @At("TAIL"))
    private void checkSpamCraft(CallbackInfo ci) {
        if (SpamCrafting.moduleEnabled() && InputUtil.isKeyPressed(client.getWindow().getHandle(), SpamCrafting.keyBinding.boundKey.getCode())) {
            // Maximum amount that can be crafted in a tick is a full inventory of an item for a single item recipe plus a stack of it already in the grid = 37 stacks.
            for (int i = 0; i < 37 && ((RecipeBookWidgetInt) this.recipeBook).duncanstools$reselect(true); i++) {
                this.client.interactionManager.clickSlot(this.handler.syncId, 0, 1, SlotActionType.THROW, this.client.player);
            }
        }
    }
}
