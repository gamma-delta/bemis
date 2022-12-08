package at.petrak.bemis.api.book;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

/**
 * Configuration for a book. This is what's loaded from {@code bemis.xml}.
 */
public final class BemisBookConfig {
    private String title;
    private BemisBookPath landing;

    public BemisBookConfig(
        String title,
        BemisBookPath landing
    ) {
        this.title = title;
        this.landing = landing;
    }

    public static BemisBookConfig load(JsonObject json) {
        var title = GsonHelper.getAsString(json, "title", "Give your book a title silly");
        var landing = GsonHelper.getAsString(json, "landing", "landing");

        return new BemisBookConfig(title, BemisBookPath.parse(landing));
    }

    public String title() {
        return title;
    }

    public BemisBookPath landing() {
        return landing;
    }
}
