package at.petrak.bemis.api;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class XmlHelper {
    /**
     * Helper method to avoid having to make new XPath objects via a factory.
     *
     * @return the result of getting the nodes at the XPath.
     */
    public static List<Node> getManyXpath(Node node, String xpathExpr) {
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            XPathNodes nodes = xpath.evaluateExpression(xpathExpr, node, XPathNodes.class);

            var out = new ArrayList<Node>(nodes.size());
            for (int i = 0; i < nodes.size(); i++) {
                out.add(nodes.get(0));
            }
            return out;
        } catch (XPathException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Helper method to avoid having to make new XPath objects via a factory.
     * Unlike {@code getMany}, this returns one single node.
     *
     * @return the result of getting the nodes at the XPath, or null if there wasn't exactly one there.
     */
    @Nullable
    public static Node getXpath(Node node, String xpathExpr) {
        var nodes = getManyXpath(node, xpathExpr);
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get a "reasonable" XML document parser that doesn't do weird things like try to load stuff
     * from the network. (Jesus christ why is that in spec).
     *
     * <ul>
     *     <li>
     *         No expanding of entity references. See <a href="https://www.youtube.com/watch?v=gjm6VHZa_8s">this video</a>
     *         for why that would be a Very Bad Idea.
     *     </li>
     *     <li>
     *         Not namespace-aware, so we can go {@code <bemis:text>} without it being interpreted as an
     *         XML namespace.
     *     </li>
     *     <li>
     *         Is coalescing, which means that you can write CDATA nodes and they'll act more like text nodes.
     *     </li>
     *     <li>
     *         Strips comments, so trying to iterate things won't get the comments.
     *     </li>
     * </ul>
     */
    public static DocumentBuilder reasonableParser() {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(false);
        factory.setCoalescing(true);
        factory.setIgnoringComments(true);

        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("this should never happen", e);
        }
    }

    /**
     * Load a {@link Node} using the above reasonable parser.
     */
    public static Node loadNode(InputStream is) {
        var parser = reasonableParser();
        try {
            // The "node" loaded is the entire document
            // That node's (sole) child is the root node, so we remove the level of indirection here for expected
            // behavior
            return parser.parse(is).getFirstChild();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
