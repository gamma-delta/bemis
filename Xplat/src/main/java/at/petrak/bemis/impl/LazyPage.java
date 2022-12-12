package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.impl.adoc.BemisAdocConverter;
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
                Component.literal("IO exception " + this.fileLoc),
                List.of(new ErrorVerse("IO exception at " + this.fileLoc, exn)));
        }

        // God bless the garbage collector
        var out = new BemisAdocConverter.Out();
        BemisBookRegistry.ASCIIDOCTOR.convert(src,
            Options.builder()
                .backend("bemis")
                .toFile(false)
                .option(BemisApi.OUTPUT_SMUGGLING_SENTINEL, out)
                .build(),
            BemisPage.class);
        return this.page = out.getPage();
    }

    /**
     * Invalidate this page and force it to re-load.
     */
    public void invalidate() {
        this.page = null;
    }
}
