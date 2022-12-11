package at.petrak.bemis.api;

import net.minecraft.client.gui.Font;

public final class BemisDrawCtx {
    public final Font font;
    public final int width;

    /**
     * Screen-space X-pos of the left corner
     */
    public int x;

    /**
     * Screen-space Y-pos of the top corner
     */
    public int y;

    public BemisDrawCtx(Font font, int width, int x, int y) {
        this.font = font;
        this.width = width;
        this.x = x;
        this.y = y;
    }
}
