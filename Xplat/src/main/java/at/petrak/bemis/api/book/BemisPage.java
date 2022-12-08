package at.petrak.bemis.api.book;

import net.minecraft.network.chat.Component;

import java.util.List;

public record BemisPage(Component title, List<BemisVerse> verses) {
}
