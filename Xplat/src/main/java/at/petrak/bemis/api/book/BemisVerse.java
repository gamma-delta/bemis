package at.petrak.bemis.api.book;

import at.petrak.bemis.api.BemisDrawCtx;
import com.mojang.blaze3d.vertex.PoseStack;

import java.io.Writer;

/**
 * A section in a {@link BemisPage}, such as a block of text, an image, a crafting recipe...
 */
public interface BemisVerse {
    /**
     * Render this to the screen. The {@link PoseStack} is set up such that {@code 0,0} is the top-left corner of the
     * writable area this verse is entitled to.
     *
     * @return the height in pixels this verse used up.
     */
    int draw(PoseStack ps, BemisDrawCtx ctx);

    /**
     * Render this to HTML somehow
     */
    void writeHTML(Writer htmlOut);
}
