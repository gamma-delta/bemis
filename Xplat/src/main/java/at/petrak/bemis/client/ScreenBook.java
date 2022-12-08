package at.petrak.bemis.client;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisBookPath;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.impl.RecManResourceLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

public class ScreenBook extends Screen {
    protected final BemisBook book;
    protected BemisBookPath path;

    protected BemisPage currentPage;

    protected VersesWidget wigVerses;

    protected IBemisResourceLoader loader;

    public ScreenBook(BemisBook book) {
        super(Component.translatable(book.getConfig().title()));
        this.book = book;
        this.path = book.getConfig().landing();

    }

    protected void updatePageToPath() {
        this.currentPage = this.book.loadPage(this.path, this.loader);
    }

    @Override
    protected void init() {
        this.wigVerses = this.addRenderableWidget(new VersesWidget());
        this.loader = new RecManResourceLoader(Minecraft.getInstance().getResourceManager());

        this.updatePageToPath();
    }

    public class VersesWidget implements Widget, GuiEventListener, NarratableEntry {
        @Override
        public void render(PoseStack ps, int mx, int my, float partialTicks) {
            int width = Math.round(ScreenBook.this.width * 0.8f);
            var ctx = new BemisDrawCtx(ScreenBook.this.minecraft.font, width);

            ps.pushPose();
            ps.translate((ScreenBook.this.width - width) / 2.0, ScreenBook.this.height * 0.1, 100);

            int cursorDown = 0;

            List<BemisVerse> verses = ScreenBook.this.currentPage.verses();
            for (int i = 0; i < verses.size(); i++) {
                BemisVerse verse = verses.get(i);
                ps.pushPose();
                ps.translate(0, cursorDown, 0);

                var dy = verse.draw(ps, ctx);

                if (Screen.hasAltDown()) {
                    // mix the colors a little
                    RenderHelper.renderQuadLines(ps, 0, 0, width, dy,
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
