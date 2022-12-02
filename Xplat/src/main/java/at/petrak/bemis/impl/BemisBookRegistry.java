package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.asciidoctor.Asciidoctor;

import java.util.HashMap;
import java.util.Map;

/**
 * A listing of all the books Bemis knows about.
 */
public final class BemisBookRegistry {
    private static final Map<ResourceLocation, BookSkeleton> KNOWN_BOOKS = new HashMap<>();

    public static final Asciidoctor ASCIIDOCTOR = Asciidoctor.Factory.create();

    /**
     * Load all the books from disc.
     */
    public static void scanAndLoadBooks(ResourceManager recman) {
        KNOWN_BOOKS.clear();

        var loader = new RecManNodeLoader(recman);
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
    }
}
