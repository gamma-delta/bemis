package at.petrak.bemis.api.book;

import at.petrak.bemis.api.BemisDrawCtx;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * A section in a {@link BemisPage}, such as a block of text, an image, a crafting recipe...
 */
public interface BemisVerse {
    int DEFAULT_VERTICAL_PADDING = 10;

    /**
     * Render this to the screen. The {@link PoseStack} is set up such that {@code 0,0} is the top-left corner of the
     * writable area this verse is entitled to.
     * <p>
     * This is called once before the screen is presented to the player, and then once each frame.
     * The first time is for Bemis' benefit, to record the height this verse requires.
     * You can also use it for your own initialization -- check {@code ctx.isInit()}.
     *
     * @return the height in pixels this verse used up. A given instance of a verse MUST return the same
     * height every time this is called, or bad things will happen.
     */
    int draw(PoseStack ps, BemisDrawCtx ctx);

    /**
     * Called when the user clicks on this verse. The default implementation is a no-op.
     *
     * @param cornerX The screen-space X position of the top-left corner of the verse
     * @param cornerY The screen-space Y position of the top-left corner of the verse
     * @param mouseX  The screen-space X position of the mouse pos
     * @param mouseY  The screen-space X position of the mouse pos
     * @param button  Which mouse button was pressed
     */
    default void onClick(int cornerX, int cornerY, double mouseX, double mouseY, int button) {
    }
}
