package at.petrak.bemis.api.book;

import at.petrak.bemis.api.IndexTreeNode;
import at.petrak.bemis.impl.LazyPage;
import net.minecraft.resources.ResourceLocation;

/**
 * A loaded book, ready to read.
 */
public final class BemisBook {
    private ResourceLocation bookLoc;
    private BemisBookConfig config;

    private IndexTreeNode<LazyPage> pages;

    private BemisBook(ResourceLocation bookLoc, BemisBookConfig config, IndexTreeNode<LazyPage> pages) {
        this.bookLoc = bookLoc;
        this.config = config;
        this.pages = pages;
    }
}
