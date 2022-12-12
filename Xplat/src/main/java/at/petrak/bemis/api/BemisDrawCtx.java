package at.petrak.bemis.api;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

/**
 * Information about drawing.
 */
public interface BemisDrawCtx {
    Font font();

    int width();

    int x();

    int y();

    double mouseX();

    double mouseY();

    ResourceLocation bookTexture();

    /**
     * Return whether this is being "drawn" the first time to initialize and record the height.
     */
    boolean isInit();
}
