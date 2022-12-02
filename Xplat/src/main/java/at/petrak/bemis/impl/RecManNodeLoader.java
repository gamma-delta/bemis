package at.petrak.bemis.impl;

import at.petrak.bemis.api.IBemisResourceLoader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

/**
 * Load XML nodes from the resource manager
 */
public record RecManNodeLoader(ResourceManager recman) implements IBemisResourceLoader {
    @Override
    public JsonObject loadJson(ResourceLocation location) throws IOException, JsonParseException {
        var maybeResource = recman.getResource(location);
        if (maybeResource.isEmpty()) {
            throw new IOException("there was no resource at " + location);
        }

        return GsonHelper.parse(maybeResource.get().openAsReader());
    }

    @Override
    public String loadFile(ResourceLocation location) throws IOException {
        var maybeResource = recman.getResource(location);
        if (maybeResource.isEmpty()) {
            throw new IOException("there was no resource at " + location);
        }

        return IOUtils.toString(maybeResource.get().openAsReader());
    }

    @Override
    public List<ResourceLocation> getAllChildren(ResourceLocation root) {
        // https://github.com/VazkiiMods/Patchouli/blob/1.19.x/Xplat/src/main/java/vazkii/patchouli/client/book/BookContentResourceDirectLoader.java#L38
        var files = recman.listResources(root.getPath(), p ->
            p.getNamespace().equals(root.getNamespace()));
        return List.copyOf(files.keySet());
    }
}
