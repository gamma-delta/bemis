package at.petrak.bemis.api;

import com.mojang.blaze3d.vertex.PoseStack;
import org.w3c.dom.Node;

import java.io.Writer;

/**
 * A section in a {@link BemisPage}, such as a block of text, an image, a crafting recipe...
 */
public interface BemisVerse {
    /**
     * Load the data from an XML node.
     * <p>
     * This should completely clobber any data previously on this verse.
     *
     * @throws IllegalArgumentException if the Node passed is malformed
     */
    void load(Node node, BemisBook book, String thisPath) throws IllegalArgumentException;

    /**
     * Render this to the screen. The {@link PoseStack} is set up such that {@code 0,0} is the top-left corner of the
     * writable area this verse is entitled to.
     *
     * @param width The maximum width in pixels this verse is to be rendered within.
     * @return the height in pixels this verse used up.
     */
    int draw(PoseStack ps, IBemisDrawCtx ctx);

    /**
     * Render this to HTML somehow
     */
    void writeHTML(Writer htmlOut);
}
