package at.petrak.bemis.api.book;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.impl.RecManNodeLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

/**
 * A loaded book, ready to read.
 * <p>
 * Once a folder in a {@code bemis_books} folder with a {@code bemis.xml} is found, this is how the book is loaded:
 * <ol>
 *     <li>
 *         A {@link BemisBookConfig} is loaded from the {@code bemis.xml} file.
 *     </li>
 *     <li>
 *         The root folder is recursively scanned for XML files and folders, and the structure is all put into a
 *          {@link BemisIndex.Skeleton}.
 *     </li>
 *     <li>
 *         Each file in the skeleton is processed into a {@link BemisPage}, turning each node in the {@code /page/content}
 *         node into a {@link BemisVerse}. This produces a {@link BemisIndex.Filled}.
 *     </li>
 *     <li>The book is loaded!</li>
 * </ol>
 */
public final class BemisBook {
    private ResourceLocation bookLoc;
    private BemisBookConfig config;
    private BemisIndex.Filled index;

    /**
     * Assumes that there's already an XML definer file checked for. Pass in the loc for the book, not the defn.
     */
    public static BemisBook load(ResourceManager recman, ResourceLocation bookLoc) throws IOException {
        var loaderabstr = new RecManNodeLoader(recman);
        var bookDefnPath = BemisApi.get().toBookDefiner(bookLoc);

        var defnNode = loaderabstr.loadXml(bookDefnPath);
        var cfg = BemisBookConfig.load(defnNode);

        var skeleton = BemisIndex.Skeleton.load(loaderabstr, bookLoc);

        throw new NotImplementedException();
    }
}
