package me.duncanruns.duncanstools.booktradefinder;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FindingTradeScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager.png");

    protected FindingTradeScreen() {
        super(Text.of("Finding Enchanted Book..."));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int i = (this.width - 276) / 2;
        int j = (this.height - 166) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0.0f, 0.0f, 276, 166, 512, 256);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
