package at.petrak.bemis;

import at.petrak.bemis.impl.BookSkeleton;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

public class TestScratchpad {
    @Test
    public void loadSkeleton() throws Exception {
        var loader = new TestResourceNodeLoader();
        var skelly = BookSkeleton.load(loader, new ResourceLocation("bemis", "testbook"));
        System.out.println(skelly);
    }
}
