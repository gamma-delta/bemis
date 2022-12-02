package at.petrak.bemis.api.book;

import at.petrak.bemis.api.IndexTreeNode;
import at.petrak.bemis.api.verses.TextVerse;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * A book ready to read!
 * <p>
 * The page content is not loaded until the page is actually opened.
 */
public final class BemisBook {
    private final ResourceLocation bookLoc;
    private final BemisBookConfig config;

    private final IndexTreeNode<BemisPage> pages;

    private BemisBook(ResourceLocation bookLoc, BemisBookConfig config, IndexTreeNode<BemisPage> pages) {
        this.bookLoc = bookLoc;
        this.config = config;
        this.pages = pages;
    }

    public ResourceLocation getBookLoc() {
        return bookLoc;
    }

    public BemisBookConfig getConfig() {
        return config;
    }

    public BemisPage loadPage(BemisBookPath path) {
        var tree = this.pages.get(path);

        if (tree == null || tree.item().isEmpty()) {
            return new BemisPage(
                Component.literal("Nothing here?"),
                List.of(new TextVerse("There's no page at the path " + path))
            );
        }
        return tree.item().get();
    }
}
