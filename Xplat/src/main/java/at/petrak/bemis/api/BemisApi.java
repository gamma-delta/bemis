package at.petrak.bemis.api;

import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BemisApi {
    public static final String MOD_ID = "bemis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final Supplier<IBemisApi> INSTANCE = Suppliers.memoize(() -> {
        var providers = ServiceLoader.load(IBemisApi.class).stream().toList();
        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                "There should be exactly one BemisApi implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            return provider.get();
        }
    });

    /**
     * Get an instance of the Bemis API.
     */
    public static IBemisApi get() {
        return INSTANCE.get();
    }

    /**
     * The actual interface to the API.
     */
    public interface IBemisApi {
        /**
         * Get the registry for registering new verse types.
         */
        Registry<BemisVerseType<?>> getVerseTypeRegistry();
    }
}
