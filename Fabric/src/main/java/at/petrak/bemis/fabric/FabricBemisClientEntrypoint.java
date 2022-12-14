package at.petrak.bemis.fabric;

import at.petrak.bemis.core.impl.BemisBookRegistry;
import at.petrak.bemis.core.adoc.RegisterAdocStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FabricBemisClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RegisterAdocStuff.register();

        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> BemisBookRegistry.scanAndLoadBooks(mc.getResourceManager()));
    }
}
