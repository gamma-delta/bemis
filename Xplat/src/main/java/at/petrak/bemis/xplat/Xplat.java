package at.petrak.bemis.xplat;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisVerseType;
import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;

import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Xplat {
    Supplier<Xplat> INSTANCE = Suppliers.memoize(() -> {
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

    static Xplat get() {
        return INSTANCE.get();
    }

    //

    Registry<BemisVerseType<?>> getVerseTypeRegistry();
}
