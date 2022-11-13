package at.petrak.bemis.client;

import at.petrak.bemis.api.book.BemisBook;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ScreenBook extends Screen {
    private BemisBook book;

    public ScreenBook(BemisBook book) {
        super(Component.empty());
    }
}
