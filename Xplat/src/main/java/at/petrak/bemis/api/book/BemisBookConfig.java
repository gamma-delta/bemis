package at.petrak.bemis.api.book;

import at.petrak.bemis.api.XmlHelper;
import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

import static at.petrak.bemis.api.BemisApi.modLoc;

/**
 * Configuration for a book. This is what's loaded from {@code bemis.xml}.
 */
public record BemisBookConfig(
    String landing,
    Map<String, ResourceLocation> verseShortcodes
) {

    public static BemisBookConfig load(Node node) {
        var landing = "landing";
        var verseShortcodes = defaultVerseShortcodes();

        var landingAtNode = XmlHelper.getXpath(node, "/book/landing/@at");
        if (landingAtNode != null) {
            landing = landingAtNode.getNodeValue();
        }

        var shortcodeVersesNode = XmlHelper.getXpath(node, "/book/shortcodes/verses");
        if (shortcodeVersesNode != null) {
            if (shortcodeVersesNode.getAttributes().getNamedItem("nodefault") != null) {
                verseShortcodes.clear();
            }

            // Get the *non-text* nodes
            var children = XmlHelper.getManyXpath(shortcodeVersesNode, "*");
            for (var kid : children) {
                var toVal = kid.getAttributes().getNamedItem("to");
                if (toVal != null) {
                    verseShortcodes.put(kid.getNodeName(), new ResourceLocation(toVal.getNodeValue()));
                }
            }
        }

        return new BemisBookConfig(landing, verseShortcodes);
    }

    public static HashMap<String, ResourceLocation> defaultVerseShortcodes() {
        HashMap<String, ResourceLocation> verseShortcodes = new HashMap<>();
        verseShortcodes.put("p", modLoc("paragraph"));
        return verseShortcodes;
    }
}
