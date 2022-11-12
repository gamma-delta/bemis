package at.petrak.bemis.api.book;

import net.minecraft.util.Unit;
import org.w3c.dom.Node;

/**
 * Information on how to deserialize a BemisVerse from XML.
 * <p>
 * The
 *
 * @param <T>
 */
public abstract class BemisVerseType<T extends BemisVerse> {
    /**
     * Load a verse from an XML node.
     * <p>
     * A client world will be active at this time, so you can safely do things like access recipes.
     */
    public abstract T load(Node node, BemisBookConfig config, BemisIndex<Unit> index,
        String path) throws IllegalArgumentException;
}
