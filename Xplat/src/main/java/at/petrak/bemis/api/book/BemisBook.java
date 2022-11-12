package at.petrak.bemis.api.book;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.IXmlNodeLoader;
import net.minecraft.resources.ResourceLocation;
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
public class BemisBook {
    protected BemisBookConfig config;
    protected BemisIndex.Filled index;

    /**
     * Assumes that there's already an XML definer file checked for. Pass in the loc for the book, not the defn.
     */
    public static BemisBook load(IXmlNodeLoader loader, ResourceLocation bookLoc) throws IOException {
        var bookDefnPath = BemisApi.get().toBookDefiner(bookLoc);
        var defnNode = loader.loadXml(bookDefnPath);

        var cfg = BemisBookConfig.load(defnNode);
        var skeleton = BemisIndex.Skeleton.load(loader, bookLoc);

        BemisApi.LOGGER.info("use {} {}", cfg, skeleton);
        throw new NotImplementedException();
    }
}
