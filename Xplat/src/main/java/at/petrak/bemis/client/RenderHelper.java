package at.petrak.bemis.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import net.minecraft.client.renderer.GameRenderer;

public class RenderHelper {
    public static void renderColorQuad(PoseStack ps, float x, float y, float w, float h, int color) {
        ps.pushPose();
        ps.translate(0, 0, -1);

        var mat = ps.last().pose();
        var tess = Tesselator.getInstance();
        var buf = tess.getBuilder();

        var prevShader = RenderSystem.getShader();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buf.vertex(mat, x, y, 0).color(color).endVertex();
        buf.vertex(mat, x, y + h, 0).color(color).endVertex();
        buf.vertex(mat, x + w, y + h, 0).color(color).endVertex();
        buf.vertex(mat, x + w, y, 0).color(color).endVertex();

        tess.end();
        RenderSystem.setShader(() -> prevShader);
        ps.popPose();
    }

    // https://github.com/gamma-delta/omegaquad/blob/8a52e915b30266ba138121459cf69522351d561e/src/utils/draw.rs#L19
    public static FloatFloatPair widthHeightDeficit(float screenWidth, float screenHeight, float aspectRatio) {
        if (screenWidth / screenHeight > aspectRatio) {
            var expectedWidth = screenHeight * aspectRatio;
            return FloatFloatPair.of(screenWidth - expectedWidth, 0f);
        } else {
            var expectedHeight = screenHeight / aspectRatio;
            return FloatFloatPair.of(0f, screenHeight - expectedHeight);
        }
    }
}
