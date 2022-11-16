package at.petrak.bemis.api.verses;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ErrorVerse extends TextVerse {
    public ErrorVerse(String text) {
        super(Component.literal("error: " + text).withStyle(ChatFormatting.DARK_RED));
    }
}
