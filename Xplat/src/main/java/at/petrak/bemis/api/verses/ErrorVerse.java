package at.petrak.bemis.api.verses;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ErrorVerse extends TextVerse {
    public ErrorVerse(Exception e) {
        super(Component.literal("An exception was thrown :(\n" + e.getMessage()).withStyle(ChatFormatting.DARK_RED));
    }
}
