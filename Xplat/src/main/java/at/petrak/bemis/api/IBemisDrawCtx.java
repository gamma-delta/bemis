package at.petrak.bemis.api;

import net.minecraft.client.gui.Font;

public interface IBemisDrawCtx {
    Font font();

    /**
     * The width in pixels this Verse has to be drawn in.
     */
    int width();
}
