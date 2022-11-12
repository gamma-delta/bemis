package at.petrak.bemis;

import at.petrak.bemis.api.book.BemisBook;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

public class Scratchpad {
    @Test
    public void loadBook() throws Exception {
        var loader = new TestResourceNodeLoader();
        var book = BemisBook.load(loader, new ResourceLocation("bemis", "testbook"));
        System.out.println(book);
    }
}
