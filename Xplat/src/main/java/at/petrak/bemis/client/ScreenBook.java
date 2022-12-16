package at.petrak.bemis.client;

import at.petrak.bemis.api.BemisRenderHelper;
import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisBookPath;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.core.BemisDrawCtxImpl;
import at.petrak.bemis.core.impl.RecManResourceLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.ObjIntConsumer;

import static at.petrak.bemis.api.BemisApi.modLoc;

public class ScreenBook extends Screen {
    public static final float CONTENT_WIDTH_PROP = 0.8f;
    public static final float CONTENT_ASPECT_RATIO = 2.0f;

    public static final int BOOK_TEX_WIDTH = 256;
    public static final int BOOK_TEX_HEIGHT = 144;
    public static final float BG_ASPECT_RATIO = (float) BOOK_TEX_WIDTH / (float) BOOK_TEX_HEIGHT;

    public static final float BG_WIDTH_PROP = CONTENT_WIDTH_PROP + 0.15f;

    protected final BemisBook book;
    protected final ResourceLocation backgroundTex;

    protected BemisBookPath path;
    protected BemisPage currentPage;

    protected VersesWidget wigVerses;

    protected IBemisResourceLoader loader;

    protected double mouseX = 0, mouseY = 0;

    public ScreenBook(BemisBook book) {
        super(Component.translatable(book.getConfig().title()));
        this.book = book;
        this.path = book.getConfig().landing();
        // TODO: per-book backgrounds
        this.backgroundTex = modLoc("textures/gui/book.png");
    }

    protected void updatePageToPath() {
        this.currentPage = this.book.loadPage(this.path, this.loader);

        var whDeficit = BemisRenderHelper.widthHeightDeficit(this.width, this.height, CONTENT_ASPECT_RATIO);
        int letterboxedW = (int) ((this.width - whDeficit.firstFloat()) * CONTENT_WIDTH_PROP);
        int letterboxedH = (int) (letterboxedW / CONTENT_ASPECT_RATIO);
        int x = (this.width - letterboxedW) / 2;
        int y = (this.height - letterboxedH) / 2;

        this.wigVerses = this.addRenderableWidget(new VersesWidget(x, y, letterboxedW, letterboxedH,
            Component.empty()));
    }

    @Override
    protected void init() {
        this.loader = new RecManResourceLoader(Minecraft.getInstance().getResourceManager());

        this.updatePageToPath();
    }

    @Override
    public void mouseMoved(double $$0, double $$1) {
        this.mouseX = $$0;
        this.mouseY = $$1;
    }

    @Override
    public void render(PoseStack ps, int mx, int my, float partialTicks) {
        this.renderBookBacking(ps);

        super.render(ps, mx, my, partialTicks);
    }

    public void renderBookBacking(PoseStack ps) {
        var whDeficit = BemisRenderHelper.widthHeightDeficit(this.width, this.height, BG_ASPECT_RATIO);
        int letterboxedW = (int) ((this.width - whDeficit.firstFloat()) * BG_WIDTH_PROP);
        int letterboxedH = (int) (letterboxedW / BG_ASPECT_RATIO);
        int x = (this.width - letterboxedW) / 2;
        int y = (this.height - letterboxedH) / 2;

        ps.pushPose();
        ps.translate(0, 0, -100);
        this.fillGradient(ps, 0, 0, this.width, this.height, 0xc0_101010, 0xd0_101010);

        ps.translate(0, 0, 1);
        BemisRenderHelper.drawTexture(ps, this.backgroundTex,
            x, y, letterboxedW, letterboxedH,
            0, 0, BOOK_TEX_WIDTH, BOOK_TEX_HEIGHT);

        ps.popPose();
    }

    public class VersesWidget extends AbstractWidget {
        protected Int2ObjectSortedMap<BemisVerse> verseHeights;
        protected int scrollDepth;
        protected int knownContentHeight;

        public VersesWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
            super($$0, $$1, $$2, $$3, $$4);

            this.scrollDepth = 0;

            // according to wikipedia, AVL is faster than RB in lookup-heavy applications
            // we construct once and lookup a bunch so that seems right
            // granted, the lookups gonna be like, 10 long, but whatever
            this.verseHeights = new Int2ObjectAVLTreeMap<>();

            var dummyPs = new PoseStack();
            dummyPs.translate(-999999, -999999, 0);

            int cursorDown = 0;
            var ctx = this.makeDrawCtx(true);

            for (var verse : ScreenBook.this.currentPage.verses()) {
                this.verseHeights.put(cursorDown, verse);
                int dy = verse.draw(dummyPs, ctx);
                cursorDown += dy;
                ctx.y += dy;
            }
            this.knownContentHeight = cursorDown;
        }

        protected BemisDrawCtxImpl makeDrawCtx(boolean isInit) {
            return new BemisDrawCtxImpl(ScreenBook.this.minecraft.font, this.width,
                this.x, this.y - this.scrollDepth,
                ScreenBook.this.mouseX, ScreenBook.this.mouseY,
                ScreenBook.this.backgroundTex, isInit);
        }

        @Override
        public boolean mouseScrolled(double $$0, double $$1, double dy) {
            // TODO: allow inverse scrolling and scroll speed changing
            var scrollAmt = (int) Math.signum(-dy) *
                (Screen.hasShiftDown() ? 1 : 15);
            this.scrollDepth += scrollAmt;

            if (this.knownContentHeight == -1) {
                // we dunno how tall it's gonna be
                this.scrollDepth = Math.max(this.scrollDepth, 0);
            } else {
                // we have an upper bound
                this.scrollDepth = Mth.clamp(this.scrollDepth, 0, this.knownContentHeight - this.height);
            }
            return true;
        }

        @Override
        public boolean mouseClicked(double mx, double my, int btn) {
            double scrollSpaceY = my - this.y + this.scrollDepth;
            var onesBelow = this.verseHeights.headMap(Mth.ceil(scrollSpaceY));
            if (!onesBelow.isEmpty()) {
                var lastKey = onesBelow.lastIntKey();
                var last = onesBelow.get(lastKey);
                var screenSpaceY = lastKey - this.scrollDepth;
                last.onClick(this.x, screenSpaceY, mx, my, btn);
            }

            return true;
        }

        @Override
        public void render(PoseStack ps, int mx, int my, float partialTicks) {

            // Draw everything below the scroll pos, plus the one before it
            if (!this.verseHeights.isEmpty()) {
                var ctx = this.makeDrawCtx(false);

                // Scissor-space is lower-left, window-pixel space
                // see forge's ScrollPanel
                double scale = ScreenBook.this.minecraft.getWindow().getGuiScale();
                RenderSystem.enableScissor(
                    (int) (this.x * scale),
                    ScreenBook.this.minecraft.getWindow().getHeight() - (int) ((this.y + this.height) * scale),
                    (int) (this.width * scale),
                    (int) (this.height * scale));

                ps.pushPose();
                ps.translate(this.x, this.y, 0);
                ps.translate(0, -this.scrollDepth, 0);

                var before = this.verseHeights.headMap(this.scrollDepth);
                var after = this.verseHeights.tailMap(this.scrollDepth);

                ObjIntConsumer<BemisVerse> draw = (verse, y) -> {
                    ps.pushPose();
                    ps.translate(0, y, 0);

                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    var dy = verse.draw(ps, ctx);
                    if (Screen.hasAltDown()) {
                        ps.translate(0, 0, -1);
                        BemisRenderHelper.renderColorQuad(ps, 0, 0, this.width, dy, verse.hashCode() | 0xff_303030);
                    }

                    ps.popPose();
                    ctx.y = y + dy - this.scrollDepth + this.y;
                };

                if (!before.isEmpty()) {
                    var lastKey = before.lastIntKey();
                    var lastVerse = before.get(lastKey);
                    draw.accept(lastVerse, lastKey);
                }
                after.forEach((y, verse) -> draw.accept(verse, y));

                ps.popPose();
                RenderSystem.disableScissor();
            }

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
