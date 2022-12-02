package at.petrak.bemis;

import at.petrak.bemis.api.IBemisResourceLoader;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads nodes from the test resources.
 */
public record TestResourceNodeLoader() implements IBemisResourceLoader {
    @Override
    public JsonObject loadJson(ResourceLocation location) throws IOException, JsonParseException {
        var path = toPath(location);
        var reader = Files.newBufferedReader(path, Charset.defaultCharset());
        return GsonHelper.parse(reader);
    }

    @Override
    public String loadFile(ResourceLocation location) throws IOException {
        var path = toPath(location);
        var reader = Files.newBufferedReader(path, Charset.defaultCharset());
        var str = IOUtils.toString(reader);
        reader.close();
        return str;
    }

    @Override
    public List<ResourceLocation> getAllChildren(ResourceLocation root) {
        var rootPath = toPath(root);
        Path fsRootPath;
        try {
            fsRootPath = Path.of(ClassLoader.getSystemResource("assets").toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }

        try {
            var stream = Files.walk(rootPath).map(p -> {
                // Presumably, this path will be a superstring of the rootpath
                // .../assets/bemis/bemis_books/testbook -> .../assets/bemis/bemis_books/testbook/page.xml
                var abs = p.toAbsolutePath();
                Preconditions.checkArgument(abs.startsWith(fsRootPath),
                    "class loader returned a path %s that wasn't a superpath of the system root %s", abs, fsRootPath);
                var ending = fsRootPath.relativize(abs);

                var ns = ending.getName(0).toString();
                var rlPathBob = new StringBuilder();
                for (int i = 1; i < ending.getNameCount(); i++) {
                    rlPathBob.append(ending.getName(i));
                    if (i < ending.getNameCount() - 1) {
                        rlPathBob.append('/');
                    }
                }
                return new ResourceLocation(ns, rlPathBob.toString());
            });
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    private static Path toPath(ResourceLocation path) {
        var rawPath = String.format("assets/%s/%s", path.getNamespace(), path.getPath());
        var url = ClassLoader.getSystemResource(rawPath);
        try {
            return Path.of(url.toURI()).toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
