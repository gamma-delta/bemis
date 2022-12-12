package at.petrak.bemis.api;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BemisRenderHelper {
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

    // https://github.com/VazkiiMods/Patchouli/blob/1.19.x/Xplat/src/main/java/vazkii/patchouli/client/RenderHelper.java

    public static void renderItemStackInGui(PoseStack ms, ItemStack stack, int x, int y) {
        transferMsToGl(ms, () -> Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y));
    }

    /**
     * Temporary shim to allow methods such as
     * {@link net.minecraft.client.renderer.entity.ItemRenderer#renderAndDecorateItem}
     * to support matrixstack transformations. Hopefully Mojang finishes this migration up...
     * Transfers the current CPU matrixstack to the openGL matrix stack, then runs the provided function
     * Assumption: the "root" state of the MatrixStack is same as the currently GL state,
     * such that multiplying the MatrixStack to the current GL matrix state will get us where we want to be.
     * If there have been intervening changes to the GL matrix state since the MatrixStack was constructed, then this
     * won't work.
     */
    public static void transferMsToGl(PoseStack ms, Runnable toRun) {
        PoseStack mvs = RenderSystem.getModelViewStack();
        mvs.pushPose();
        mvs.mulPoseMatrix(ms.last().pose());
        RenderSystem.applyModelViewMatrix();
        toRun.run();
        mvs.popPose();
        RenderSystem.applyModelViewMatrix();
    }


    /**
     * This function mostly exists because I can't get argument name mappings in here and it's pissing me off
     */
    public static void drawTexture(PoseStack ps, ResourceLocation texture, int x, int y, int w, int h, int u, int v,
        int uw, int vh, int texW, int texH) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        Gui.blit(ps,
            x, y, w, h,
            u, v, uw, vh,
            texW, texH);
    }

    public static void drawTexture(PoseStack ps, ResourceLocation texture, int x, int y, int w, int h, int u, int v,
        int uw, int vh) {
        drawTexture(ps, texture, x, y, w, h, u, v, uw, vh, 256, 256);
    }
}
