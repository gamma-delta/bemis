package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.IXmlNodeLoader;
import at.petrak.bemis.api.XmlHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;

/**
 * Load XML nodes from the resource manager
 */
public record RecManNodeLoader(ResourceManager recman) implements IXmlNodeLoader {
    @Override
    public Node loadXml(ResourceLocation location) {
        var maybeResource = recman.getResource(location);
        if (maybeResource.isEmpty()) {
            return null;
        }

        try {
            return XmlHelper.loadNode(maybeResource.get().open());
        } catch (IOException e) {
            BemisApi.LOGGER.warn("Couldn't load the XML file at {} for some reason", location, e);
            return null;
        }
    }

    @Override
    public List<ResourceLocation> getAllChildren(ResourceLocation root) {
        // https://github.com/VazkiiMods/Patchouli/blob/1.19.x/Xplat/src/main/java/vazkii/patchouli/client/book/BookContentResourceDirectLoader.java#L38
        var files = recman.listResources(root.getPath(), p ->
            p.getNamespace().equals(root.getNamespace()) && p.getPath().endsWith(".xml"));
        return List.copyOf(files.keySet());
    }
}
