package at.petrak.bemis.api;

import org.w3c.dom.Node;

/**
 * Information on how to deserialize a BemisVerse from XML.
 *
 * @param <T>
 */
public abstract class BemisVerseType<T extends BemisVerse> {
    public abstract T load(Node node, BemisBook bookIn, String path) throws IllegalArgumentException;
}
