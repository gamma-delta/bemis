package at.petrak.bemis.impl;

import at.petrak.bemis.api.IXmlNodeLoader;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.verses.ErrorVerse;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Lazy-loaded page location and contents.
 */
public class LazyPage {
    public final ResourceLocation xmlLoc;
    protected @Nullable BemisPage page;

    public LazyPage(ResourceLocation xmlLoc) {
        this.xmlLoc = xmlLoc;
        this.page = null;
    }

    /**
     * Lazily load the contents of the page.
     */
    public BemisPage load(IXmlNodeLoader loader) {
        if (this.page != null) return this.page;

        var node = loader.loadXml(this.xmlLoc);
        if (node == null) {
            return new BemisPage(
                Component.literal("Unknown xml page " + this.xmlLoc),
                List.of());
        }
        try {
            var page = BemisPage.load(node);
            this.page = page;
            return page;
        } catch (Exception e) {
            e.printStackTrace();
            return new BemisPage(
                Component.literal(e.getClass().getSimpleName()),
                List.of(new ErrorVerse(e))
            );
        }
    }

    /**
     * Invalidate this page and force it to re-load.
     */
    public void invalidate() {
        this.page = null;
    }
}
