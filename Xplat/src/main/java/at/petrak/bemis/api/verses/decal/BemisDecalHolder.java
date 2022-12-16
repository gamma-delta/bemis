package at.petrak.bemis.api.verses.decal;

import at.petrak.bemis.api.BemisDrawCtx;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for a bunch of {@link BemisDecal}s. One verse should probably only have one holder.
 */
public final class BemisDecalHolder {
    private final int width;
    private final int height;
    private final List<BemisDecal> decals;

    /**
     *
     */
    public BemisDecalHolder(int width, int height) {
        this.width = width;
        this.height = height;
        this.decals = new ArrayList<>();
    }

    public void draw(PoseStack ps, BemisDrawCtx ctx) {
        // for graphics reasons there need to be 4 coords
        // xy is mx my, z is always 0, w is ignored but has to be 1 for math reasons
        Vector4f mouseVec = new Vector4f();

        for (var decal : this.decals) {
            ps.pushPose();
            ps.translate(decal.x, decal.y, 0);

            var lastMat = ps.last().pose().copy();
            lastMat.invert();
            mouseVec.set((float) ctx.mouseX(), (float) ctx.mouseY(), 1, 1);
            mouseVec.transform(lastMat);

            // this is post-transformation, so don't add the x and y
            boolean hovering =
                0 <= mouseVec.x() && mouseVec.x() <= decal.width
                    && 0 <= mouseVec.y() && mouseVec.y() <= decal.height;

            decal.draw(ps, ctx, mouseVec.x(), mouseVec.y(), hovering);

            ps.popPose();
        }

    }

    public <T extends BemisDecal> T addDecal(T decal) {
        this.decals.add(decal);
        return decal;
    }


    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public List<BemisDecal> decals() {
        return decals;
    }
}
