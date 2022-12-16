package at.petrak.bemis.api.verses.decal;

import at.petrak.bemis.api.BemisDrawCtx;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * An abstraction layer to help you write Verses.
 * <p>
 * It's very common to have
 * <ul>
 *     <li>a collection of GUI elements,</li>
 *     <li>which are logically linked,</li>
 *     <li>and all need hover/click behavior.</li>
 * </ul>
 * <p>
 * This class helps you write those, wrapping up the math for you all nice.
 */
public abstract class BemisDecal {
    protected final int x, y, width, height;

    public BemisDecal(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Implementors should implement this.
     * <p>
     * The {@link BemisDrawCtx} passed in has <em>global</em> geometric information.
     * The params to the function are in the space of the decal itself, with {@code 0,0} being the top-left corner.
     * The PoseStack is pre-set-up for that.
     */
    abstract protected void draw(PoseStack ps, BemisDrawCtx ctx, float mx, float my, boolean isHovered);
}
