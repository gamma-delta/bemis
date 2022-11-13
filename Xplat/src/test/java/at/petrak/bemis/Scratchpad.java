package at.petrak.bemis;

import at.petrak.bemis.api.book.BemisBookConfig;
import at.petrak.bemis.api.book.BemisIndex;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

public class Scratchpad {
    @Test
    public void loadCfg() throws Exception {
        var loader = new TestResourceNodeLoader();
        var cfg = BemisBookConfig.load(loader.loadXml(new ResourceLocation("bemis", "bemis_books/testbook/bemis.xml")));
        System.out.println(cfg);
    }

    @Test
    public void loadSkeleton() throws Exception {
        var loader = new TestResourceNodeLoader();
        var skelly = BemisIndex.Skeleton.load(loader, new ResourceLocation("bemis", "testbook"));
        System.out.println(skelly);
    }
}
