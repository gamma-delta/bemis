package at.petrak.bemis.impl;

import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.IndexTreeNode;
import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisBookConfig;
import at.petrak.bemis.api.book.BemisBookPath;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.api.verses.TextVerse;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * We split this into the abstract {@link BemisBook} and this impler so we don't have
 * {@link LazyPage} in the API
 */
public class BemisBookImpl extends BemisBook {
    private final ResourceLocation bookLoc;
    private final BemisBookConfig config;

    private final IndexTreeNode<LazyPage> pages;

    public BemisBookImpl(ResourceLocation bookLoc, BemisBookConfig config, IndexTreeNode<LazyPage> pages) {
        this.bookLoc = bookLoc;
        this.config = config;
        this.pages = pages;
    }

    @Override
    public ResourceLocation getBookLoc() {
        return bookLoc;
    }

    @Override
    public BemisBookConfig getConfig() {
        return config;
    }

    @Override
    public BemisPage loadPage(BemisBookPath path, IBemisResourceLoader loader) {
        var tree = this.pages.get(path);

        if (tree == null || tree.item().isEmpty()) {
            return new BemisPage(
                Component.literal("Nothing here?"),
                List.of(new ErrorVerse("There's no page at the path " + path))
            );
        }
        var lazy = tree.item().get();
        return lazy.load(loader);
    }
}
