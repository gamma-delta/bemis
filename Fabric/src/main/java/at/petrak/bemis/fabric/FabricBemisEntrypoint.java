package at.petrak.bemis.fabric;

import at.petrak.bemis.common.BemisRegistrar;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

public class FabricBemisEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        BemisRegistrar.registerItems((rl, i) -> Registry.register(Registry.ITEM, rl, i));
    }
}
