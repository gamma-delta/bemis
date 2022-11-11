package at.petrak.bemis.api;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BemisApi {
    String MOD_ID = "bemis";
    Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
