package at.petrak.bemis.api.book;

import at.petrak.bemis.api.IBemisResourceLoader;
import net.minecraft.resources.ResourceLocation;

/**
 * A book ready to read!
 * <p>
 * The page content is not loaded until the page is actually opened.
 */
public abstract class BemisBook {
    public abstract ResourceLocation getBookLoc();

    public abstract BemisBookConfig getConfig();

    public abstract BemisPage loadPage(BemisBookPath path, IBemisResourceLoader loader);
}
