package at.petrak.bemis.api;

import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisVerse;
import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BemisApi {
    public static final String MOD_ID = "bemis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final Supplier<IBemisApi> INSTANCE = Suppliers.memoize(() -> {
        var providers = ServiceLoader.load(IBemisApi.class).stream().toList();
        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                "There should be exactly one BemisApi implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            return provider.get();
        }
    });

    /**
     * The folder in the Minecraft resource root that Bemis books are loaded from. Each folder in <i>that</i> folder
     * is checked to see if it makes for a valid book.
     */
    public static final String BOOK_FOLDER = "bemis_books";

    /**
     * The name of the file in a book folder that holds the book information. It's the only "special" file in a book
     * folder; everything else is an adoc page.
     */
    public static final String BOOK_DEFINER = "bemis.json";

    /**
     * The name of the special string Bemis uses to identify verse block macros.
     */
    public static final String BLOCK_MACRO_SENTINEL = "BEMIS_BLOCK_MACRO_SENTINEL";

    /**
     * The extension Bemis looks for to see if something's a file that should be loaded as a page.
     */
    public static final String PAGE_EXTENSION = ".adoc";

    /**
     * Get an instance of the Bemis API.
     */
    public static IBemisApi get() {
        return INSTANCE.get();
    }

    /**
     * The actual interface to the API.
     */
    public interface IBemisApi {

        /**
         * Returns the path to the folder defining the book with the given name. This doesn't do any validation;
         * it's simply text transformation.
         * <p>
         * {@code foobar:a-book} becomes {@code foobar:bemis_books/a-book}.
         */
        default ResourceLocation toBookResourceFolder(ResourceLocation bookLoc) {
            var path = String.format("%s/%s", BemisApi.BOOK_FOLDER, bookLoc.getPath());
            return new ResourceLocation(bookLoc.getNamespace(), path);
        }

        /**
         * Returns the path to the XML file defining the book with the given name. This doesn't do any validation;
         * it's simply text transformation.
         * <p>
         * {@code foobar:a-book} becomes {@code foobar:bemis_books/a-book.xml}.
         */
        default ResourceLocation toBookDefiner(ResourceLocation bookLoc) {
            var root = toBookResourceFolder(bookLoc);
            return new ResourceLocation(root.getNamespace(), root.getPath() + "/" + BOOK_DEFINER);
        }

        /**
         * Returns the location of the book at the given definer XML file path. This doesn't do any validation;
         * it's simply text transformation.
         * <p>
         * {@code foobar:bemis_books/a-book/bemis.json} becomes {@code foobar:bemis_books/a-book}.
         * <p>
         * Returns null if it's invalid.
         */
        @Nullable
        default ResourceLocation toBookLoc(ResourceLocation defnFilePath) {
            var path = defnFilePath.getPath();
            if (!path.startsWith(BOOK_FOLDER) || !path.endsWith(BOOK_DEFINER)) {
                return null;
            }
            // Strip the folder, the slash after, the definer, and the slash before
            var slicedPath = path.substring(BOOK_FOLDER.length() + 1, path.length() - BOOK_DEFINER.length() - 1);
            return new ResourceLocation(defnFilePath.getNamespace(), slicedPath);
        }

        /**
         * Convert a list of {@link BemisVerse}s into the form Bemis uses internally to identify verse-producing macros.
         */
        Block makeVerseMacroNode(BlockMacroProcessor self, StructuralNode parent, List<BemisVerse> verses);

        /**
         * Get the Java extension registry for the global AsciiDoctor instance.
         */
        JavaExtensionRegistry getJavaExtensionRegistry();

        @Nullable BemisBook getBook(ResourceLocation bookLoc);
    }
}
