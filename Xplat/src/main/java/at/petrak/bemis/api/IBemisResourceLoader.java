package at.petrak.bemis.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.List;

/**
 * Resource locations go in, XML nodes come out. This loads <em>Minecraft</em> resource paths, not <em>Bemis</em> ones.
 * <p>
 * This lets me abstract away needing a {@link net.minecraft.server.packs.resources.ResourceManager ResourceManager}
 * for test cases.
 */
public interface IBemisResourceLoader {
    /**
     * Load the JSON file at the given location into a node. Pass it a path ending in @code{.json}.
     *
     * @throws IOException        if the file wasn't found
     * @throws JsonParseException if the file was invalid JSON.
     */
    JsonObject loadJson(ResourceLocation location) throws IOException, JsonParseException;

    /**
     * Load the string contents of the given file.
     *
     * @throws IOException if the file wasn't found.
     */
    String loadFile(ResourceLocation location) throws IOException;

    /**
     * Return a set of all the valid paths to files below the given path.
     *
     * @return
     */
    List<ResourceLocation> getAllChildren(ResourceLocation root);
}
