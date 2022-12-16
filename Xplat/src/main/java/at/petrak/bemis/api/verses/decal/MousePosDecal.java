package at.petrak.bemis.api.verses.decal;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.BemisRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;

public class MousePosDecal extends BemisDecal {
    public MousePosDecal(int x, int y) {
        super(x, y, 64, 16);
    }

    @Override
    protected void draw(PoseStack ps, BemisDrawCtx ctx, float mx, float my, boolean isHovered) {
        BemisRenderHelper.renderColorQuad(ps, 0, 0, this.width, this.height, isHovered ? 0xff_ddddaa : 0xff_cccccc);
        ctx.font().draw(ps, "%.2f,%.2f".formatted(mx, my), 0, 0, -1);
    }
}
