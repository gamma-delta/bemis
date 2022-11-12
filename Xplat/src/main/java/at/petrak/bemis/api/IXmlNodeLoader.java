package at.petrak.bemis.api;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Resource locations go in, XML nodes come out. This loads <em>Minecraft</em> resource paths, not <em>Bemis</em> ones.
 * <p>
 * This lets me abstract away needing a {@link net.minecraft.server.packs.resources.ResourceManager ResourceManager}
 * for test cases.
 */
public interface IXmlNodeLoader {
    /**
     * Load the XML file at the given location into a node, or `null` if it couldn't find the file.
     */
    @Nullable
    Node loadXml(ResourceLocation location);

    /**
     * Return a set of all the valid paths to XML files below the given path.
     *
     * @return
     */
    List<ResourceLocation> getAllChildren(ResourceLocation root);
}
