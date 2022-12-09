package at.petrak.bemis.impl;

import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.verses.TextVerse;
import at.petrak.bemis.impl.adoc.ConversionPage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.asciidoctor.Options;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * Lazy-loaded page location and contents.
 */
public class LazyPage {
    public final ResourceLocation fileLoc;
    protected @Nullable BemisPage page;

    public LazyPage(ResourceLocation fileLoc) {
        this.fileLoc = fileLoc;
        this.page = null;
    }

    /**
     * Lazily load the contents of the page.
     */
    public BemisPage load(IBemisResourceLoader loader) {
        if (this.page != null) return this.page;

        String src;
        try {
            src = loader.loadFile(this.fileLoc);
        } catch (IOException exn) {
            return new BemisPage(
                Component.literal("Unknown file path" + this.fileLoc),
                List.of());
        }

        var page = BemisBookRegistry.ASCIIDOCTOR.convert(src,
            Options.builder().backend("bemis").toFile(false).build(),
            ConversionPage.class);
        if (page instanceof ConversionPage.Doc doc) {
            this.page = doc.out;
            return this.page;
        } else {
            return new BemisPage(
                Component.literal("Returned a ConversionPage.BodyPart somehow"),
                List.of(new TextVerse("howdja do that??")));
        }
    }

    /**
     * Invalidate this page and force it to re-load.
     */
    public void invalidate() {
        this.page = null;
    }
}
