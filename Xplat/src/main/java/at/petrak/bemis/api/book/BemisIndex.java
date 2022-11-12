package at.petrak.bemis.api.book;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.IXmlNodeLoader;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A mapping of paths to things and folders of things. It's a trie!
 *
 * @param <T> the contained thing, which will be {@link ResourceLocation} when the book's layout is being calculated
 *            and {@link BemisPage} once the page content is loaded.
 */
public abstract class BemisIndex<T> {
    protected Entry<T> root;

    /**
     * What lies at the end of a path.
     * <p>
     * Either {@code item} or {@code children} (or both) must be non-null. The both case is if you have something like:
     * <pre>
     * path/to/folder/
     * - foo.xml
     * - foo/
     *   - bar.xml
     *   - baz.xml
     * </pre>
     * <p>
     * with a folder and file of the same name {@code foo}. If neither were present then there wouldn't be any point in keeping
     * the entry around in memory.
     */
    public static final class Entry<T> {
        private final @Nullable T item;
        private @Nullable Map<String, Entry<T>> children;

        public Entry(@Nullable T item, @Nullable Map<String, Entry<T>> children) {
            this.item = item;
            this.children = children;
        }

        void assertBothNonNull() {
            Preconditions.checkArgument(this.item != null || this.children != null,
                "entry can't have no item or children");
        }

        /**
         * Assuming this is the root node, create a new Entry with the given item at the given path.
         * <p>
         * Returns the new entry.
         */
        Entry<T> insertAt(T item, String path) {
            var splitted = path.split("/", 2);
            if (splitted.length == 0) {
                // then i really don't know what you're doing?
                throw new IllegalStateException("attempted to insert " + item + "at the empty path");
            } else if (splitted.length == 1) {
                // Then this is the last segment! yahoo
                if (this.children == null) {
                    this.children = new HashMap<>();
                }
                var newEntry = new Entry<>(item, null);
                this.children.put(splitted[0], newEntry);
                return newEntry;
            } else {
                // recurse
                if (this.children == null) {
                    this.children = new HashMap<>();
                }

                var subentry = new Entry<T>(null, new HashMap<>());
                var out = subentry.insertAt(item, splitted[1]);
                this.children.put(splitted[0], subentry);
                return out;
            }
        }

        public @Nullable T item() {
            return item;
        }

        public @Nullable Map<String, Entry<T>> children() {
            return children;
        }

        @Override
        public String toString() {
            return "Entry[" +
                "item=" + item + ", " +
                "children=" + children + ']';
        }

    }

    /**
     * Loads a skeleton from the resource manager.
     *
     * @return null if it can't load it for some reason
     * <p>
     * TODO check if this works
     */
    public static Skeleton load(IXmlNodeLoader loader, ResourceLocation bookLoc) {
        var rootPath = BemisApi.get().toBookResourceFolder(bookLoc);
        // Irritatingly it looks like MC wants to ignore the NS here. so we just get all the files everywhere
        // and filter them in a second
        var paths = loader.getAllChildren(rootPath);

        var root = new Entry<ResourceLocation>(null, new HashMap<>());

        for (var path : paths) {
            if (!path.getNamespace().equals(bookLoc.getNamespace())) {
                continue;
            }

            Preconditions.checkArgument(path.getPath().startsWith(rootPath.getPath()));
            Preconditions.checkArgument(path.getPath().endsWith(".xml"));

            // Don't try to treat the definer as a page
            if (path.getPath().equals(rootPath.getPath() + "/" + BemisApi.BOOK_DEFINER)) {
                continue;
            }

            // Slice up the "raw" path into the useful part
            // bemis:bemis_books/testbook/page.xml -> page
            var pathInBook = path.getPath()
                .substring(rootPath.getPath().length(), path.getPath().length() - ".xml".length());
            if (pathInBook.startsWith("/")) {
                pathInBook = pathInBook.substring(1);
            }

            // Add the node to the skellyton
            root.insertAt(path, pathInBook);
        }

        return new Skeleton(root);
    }

    /**
     * The skeletal layout of the book; we know where all the pages are, but not what's in them.
     * <p>
     * Bemis first loads this from the layout of your book folder tree. Then, once it's known what pages have
     * to be loaded, this is turned into a {@link Filled BemisIndex.Filled}.
     * <p>
     * The {@link ResourceLocation} is the resource-managed path to the file associated with that page, just to save
     * a bit of re-calculation.
     */
    public static class Skeleton extends BemisIndex<ResourceLocation> {
        protected Skeleton(Entry<ResourceLocation> root) {
            this.root = root;
        }
    }

    public static class Filled extends BemisIndex<BemisPage> {

    }
}
