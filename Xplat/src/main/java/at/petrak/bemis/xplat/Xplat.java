package at.petrak.bemis.xplat;

import at.petrak.bemis.api.BemisApi;
import net.minecraft.Util;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface Xplat {
    Xplat IT = Util.make(() -> {
        var providers = ServiceLoader.load(Xplat.class).stream().toList();
        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                "There should be exactly one Xplat implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            BemisApi.LOGGER.debug("Instantiating Bemis xplat impl: " + provider.type().getName());
            return provider.get();
        }
    });
}
