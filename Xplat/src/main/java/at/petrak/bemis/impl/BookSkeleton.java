package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.IBemisResourceLoader;
import at.petrak.bemis.api.IndexTreeNode;
import at.petrak.bemis.api.book.BemisBookConfig;
import at.petrak.bemis.api.book.BemisBookPath;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;

/**
 * Book that's had everything scanned and is ready to be turned into a {@link at.petrak.bemis.api.book.BemisBook}
 */
public record BookSkeleton(ResourceLocation bookLoc, BemisBookConfig cfg, IndexTreeNode<ResourceLocation> index) {
    public static BookSkeleton load(IBemisResourceLoader loader, ResourceLocation bookLoc) throws IOException {
        var bookDefnPath = BemisApi.get().toBookDefiner(bookLoc);

        var defnNode = loader.loadJson(bookDefnPath);
        var cfg = BemisBookConfig.load(defnNode);

        var skeleton = loadIndex(loader, bookLoc);

        return new BookSkeleton(bookLoc, cfg, skeleton);
    }

    private static IndexTreeNode<ResourceLocation> loadIndex(IBemisResourceLoader loader, ResourceLocation bookLoc) {
        var rootPath = BemisApi.get().toBookResourceFolder(bookLoc);
        // Irritatingly it looks like MC wants to ignore the NS here. so we just get all the files everywhere
        // and filter them in a second
        var paths = loader.getAllChildren(rootPath);

        var root = new IndexTreeNode<ResourceLocation>(null, new HashMap<>());

        for (var path : paths) {
            if (!path.getNamespace().equals(bookLoc.getNamespace()) || !path.getPath().endsWith(BemisApi.PAGE_EXTENSION)) {
                continue;
            }

            Preconditions.checkArgument(path.getPath().startsWith(rootPath.getPath()));

            // Don't try to treat the definer as a page
            if (path.getPath().equals(rootPath.getPath() + "/" + BemisApi.BOOK_DEFINER)) {
                continue;
            }

            // Slice up the "raw" path into the useful part
            // bemis:bemis_books/testbook/page.adoc -> page
            var pathInBook = path.getPath()
                .substring(rootPath.getPath().length(), path.getPath().length() - BemisApi.PAGE_EXTENSION.length());
            if (pathInBook.startsWith("/")) {
                pathInBook = pathInBook.substring(1);
            }
            var splitted = pathInBook.split("/");

            // Add the node to the skellyton
            root.insertAt(path, new BemisBookPath(splitted[0], splitted));
        }

        return root;
    }


}
