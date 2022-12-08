package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.asciidoctor.Asciidoctor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A listing of all the books Bemis knows about.
 */
public final class BemisBookRegistry {
    private static final Map<ResourceLocation, BookSkeleton> KNOWN_BOOKS = new HashMap<>();

    private static boolean CURRENTLY_VALID = false;

    public static final Asciidoctor ASCIIDOCTOR = Asciidoctor.Factory.create();

    /**
     * Load all the books from disc.
     */
    public static void scanAndLoadBooks(ResourceManager recman) {
        if (CURRENTLY_VALID) {
            BemisApi.LOGGER.warn("Scanned for books when the books were already valid");
        }
        KNOWN_BOOKS.clear();

        var loader = new RecManResourceLoader(recman);
        var bemises = recman.listResources(BemisApi.BOOK_FOLDER, p -> p.getPath().endsWith(BemisApi.BOOK_DEFINER));
        bemises.forEach((defnPath, resource) -> {
            try {
                var bookLoc = BemisApi.get().toBookLoc(defnPath);
                var skelly = BookSkeleton.load(loader, bookLoc);
                KNOWN_BOOKS.put(bookLoc, skelly);
            } catch (Exception e) {
                BemisApi.LOGGER.warn("An error occurred when loading the book at {}", defnPath);
                BemisApi.LOGGER.warn("Error:", e);
            }
        });

        CURRENTLY_VALID = true;
    }


    @Nullable
    public static BookSkeleton getBook(ResourceLocation bookLoc) {
        if (!CURRENTLY_VALID) {
            throw new IllegalStateException("tried to get book " + bookLoc + " when the registry was invalid");
        }
        return KNOWN_BOOKS.getOrDefault(bookLoc, null);
    }

    /**
     * Mark that the registry shouldn't be used until {@link BemisBookRegistry::scanAndLoadBooks} is called.
     */
    public static void invalidate() {
        CURRENTLY_VALID = false;
    }
}
