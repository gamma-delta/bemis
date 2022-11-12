package at.petrak.bemis.api.book;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.impl.RecManNodeLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A listing of all the books Bemis knows about.
 */
public final class BemisBookRegistry {
    private final Map<ResourceLocation, BemisBook> knownBooks = new HashMap<>();

    /**
     * Load all the books from disc.
     */
    @ApiStatus.Internal
    public static void scanAndLoadBooks(ResourceManager recman) {
        var bemises = recman.listResources(BemisApi.BOOK_FOLDER, p -> p.getPath().endsWith(BemisApi.BOOK_DEFINER));
        bemises.forEach((defnPath, resource) -> {
            try {
                var bookLoc = BemisApi.get().toBookLoc(defnPath);
                BemisBook.load(new RecManNodeLoader(recman), bookLoc);
            } catch (Exception e) {
                BemisApi.LOGGER.warn("An error occurred when loading the book at {}", defnPath);
                BemisApi.LOGGER.warn("Error:", e);
            }
        });
    }
}
