package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.decal.BemisDecalHolder;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Displays a collection of decals in the center of the allocated area.
 */
public class CenteredDecalVerse implements BemisVerse {
    protected final BemisDecalHolder decals;

    public CenteredDecalVerse(BemisDecalHolder decals) {
        this.decals = decals;
    }

    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        ps.pushPose();

        ps.translate((ctx.width() - this.decals.width()) / 2.0, 0, 0);
        this.decals.draw(ps, ctx);

        ps.popPose();
        return this.decals.height() + DEFAULT_VERTICAL_PADDING;
    }

    public static final class Builder {
    }
}
