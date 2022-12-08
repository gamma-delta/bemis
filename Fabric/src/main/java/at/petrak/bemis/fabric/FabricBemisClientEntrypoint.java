package at.petrak.bemis.fabric;

import at.petrak.bemis.impl.BemisBookRegistry;
import at.petrak.bemis.impl.adoc.BemisAdocConverter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FabricBemisClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BemisBookRegistry.ASCIIDOCTOR.javaConverterRegistry().register(BemisAdocConverter.class);

        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> BemisBookRegistry.scanAndLoadBooks(mc.getResourceManager()));
    }
}
