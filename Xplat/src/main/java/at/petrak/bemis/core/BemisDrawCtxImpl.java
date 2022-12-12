package at.petrak.bemis.core;

import at.petrak.bemis.api.BemisDrawCtx;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public class BemisDrawCtxImpl implements BemisDrawCtx {
    public final Font font;
    public final int width;
    public int x, y;
    public final double mouseX, mouseY;
    public final ResourceLocation bookTexture;
    public final boolean isInit;

    public BemisDrawCtxImpl(Font font, int width, int x, int y, double mouseX, double mouseY,
        ResourceLocation bookTexture,
        boolean isInit) {
        this.font = font;
        this.width = width;
        this.x = x;
        this.y = y;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.bookTexture = bookTexture;
        this.isInit = isInit;
    }

    @Override
    public Font font() {
        return font;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public double mouseX() {
        return mouseX;
    }

    @Override
    public double mouseY() {
        return mouseY;
    }

    @Override
    public ResourceLocation bookTexture() {
        return bookTexture;
    }

    @Override
    public boolean isInit() {
        return isInit;
    }
}
