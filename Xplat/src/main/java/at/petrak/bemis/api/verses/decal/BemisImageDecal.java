package at.petrak.bemis.api.verses.decal;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.BemisRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Displays an image.
 * <p>
 * TODO there is no way to not have a 256x256 img here
 */
public class BemisImageDecal extends BemisDecal {
    @Nullable
    protected final ResourceLocation image;
    protected final int u;
    protected final int v;
    protected final int uw;
    protected final int vh;

    /**
     * @param image If this is {@code null}, then the image used will be the book texture atlas.
     */
    public BemisImageDecal(int x, int y, int width, int height, @Nullable ResourceLocation image, int u, int v,
        int uw, int vh) {
        super(x, y, width, height);
        this.image = image;
        this.u = u;
        this.v = v;
        this.uw = uw;
        this.vh = vh;
    }

    @Override
    protected void draw(PoseStack ps, BemisDrawCtx ctx, float mx, float my, boolean isHovered) {
        BemisRenderHelper.drawTexture(ps, this.image == null ? ctx.bookTexture() : this.image,
            0, 0, this.width, this.height,
            this.u, this.v, this.uw, this.vh);
    }
}
