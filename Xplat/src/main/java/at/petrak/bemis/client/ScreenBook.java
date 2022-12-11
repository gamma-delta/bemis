package at.petrak.bemis.client;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisBookPath;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.impl.RecManResourceLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;

import static at.petrak.bemis.api.BemisApi.modLoc;

public class ScreenBook extends Screen {
    public static final float CONTENT_WIDTH_PROP = 0.8f;
    public static final float CONTENT_VERT_PADDING = 0.15f;

    public static final int BOOK_TEX_WIDTH = 256;
    public static final int BOOK_TEX_HEIGHT = 144;
    public static final float BOOK_TEX_ASPECT_RATIO = (float) BOOK_TEX_WIDTH / (float) BOOK_TEX_HEIGHT;

    public static final float BG_WIDTH_PROP = CONTENT_WIDTH_PROP + 0.15f;

    protected final BemisBook book;
    protected final ResourceLocation backgroundTex;

    protected BemisBookPath path;
    protected BemisPage currentPage;
    protected int scrollDepth;

    protected VersesWidget wigVerses;

    protected IBemisResourceLoader loader;

    public ScreenBook(BemisBook book) {
        super(Component.translatable(book.getConfig().title()));
        this.book = book;
        this.path = book.getConfig().landing();
        // TODO: per-book backgrounds
        this.backgroundTex = modLoc("textures/gui/book.png");
        this.scrollDepth = 0;
    }

    protected void updatePageToPath() {
        this.currentPage = this.book.loadPage(this.path, this.loader);
    }

    @Override
    protected void init() {
        this.loader = new RecManResourceLoader(Minecraft.getInstance().getResourceManager());

        // TODO: get wh deficit letterboxing in

        {
            int width = Math.round(this.width * CONTENT_WIDTH_PROP);
            int y = (int) (this.height * CONTENT_VERT_PADDING);
            int x = (this.width - width) / 2;
            int height = this.height - (2 * y);

            this.wigVerses = this.addRenderableWidget(new VersesWidget(x, y, width, height, Component.empty()));
        }

        this.updatePageToPath();
    }

    @Override
    public void render(PoseStack ps, int mx, int my, float partialTicks) {
        this.renderBackground(ps);
        this.renderBookBacking(ps);

        super.render(ps, mx, my, partialTicks);
    }

    public void renderBookBacking(PoseStack ps) {
        var whDeficit = RenderHelper.widthHeightDeficit(this.width, this.height, BOOK_TEX_ASPECT_RATIO);
        int letterboxedW = (int) ((this.width - whDeficit.firstFloat()) * BG_WIDTH_PROP);
        int letterboxedH = (int) (letterboxedW / BOOK_TEX_ASPECT_RATIO);
        int x = (this.width - letterboxedW) / 2;
        int y = (this.height - letterboxedH) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.backgroundTex);
        blit(ps,
            x, y, letterboxedW, letterboxedH, // x, y, w, h
            0, 0, BOOK_TEX_WIDTH, BOOK_TEX_HEIGHT, // u, v, uw, vh
            256, 256); // texture size
    }

    public class VersesWidget extends AbstractWidget {
        protected int scrollDepth = 0;

        public VersesWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
            super($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public boolean mouseScrolled(double $$0, double $$1, double dy) {
            var scrollAmt = (int) -Math.signum(dy) *
                Screen.hasShiftDown() ? 1 : 7;
            this.scrollDepth += scrollAmt;
            this.scrollDepth = Math.max(0, this.scrollDepth);
            return true;
        }

        @Override
        public void render(PoseStack ps, int mx, int my, float partialTicks) {
            int width = Math.round(ScreenBook.this.width * CONTENT_WIDTH_PROP);
            var ctx = new BemisDrawCtx(ScreenBook.this.minecraft.font, width);

            ps.pushPose();
            ps.translate((ScreenBook.this.width - width) / 2.0, ScreenBook.this.height * CONTENT_VERT_PADDING, 100);
            ps.translate(0, this.scrollDepth, 0);

            int cursorDown = 0;

            List<BemisVerse> verses = ScreenBook.this.currentPage.verses();
            for (int i = 0; i < verses.size(); i++) {
                BemisVerse verse = verses.get(i);
                ps.pushPose();
                ps.translate(0, cursorDown, 0);

                var dy = verse.draw(ps, ctx);

                if (Screen.hasAltDown()) {
                    // mix the colors a little
                    RenderHelper.renderColorQuad(ps, 0, 0, width, dy,
                        Double.hashCode(Mth.sin(i + 1)) | 0xff_000000);
                }

                cursorDown += dy;

                ps.popPose();
            }

            ps.popPose();
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.HOVERED;
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {

        }
    }
}
