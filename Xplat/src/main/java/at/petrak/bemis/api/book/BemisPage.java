package at.petrak.bemis.api.book;

import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.w3c.dom.Node;

import java.util.List;

public record BemisPage(Component title, List<BemisVerse> verses) {
    public static BemisPage load(Node node) throws Exception {
        throw new NotImplementedException();
    }
}
