package at.petrak.bemis.api.verses;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ErrorVerse extends TextVerse {
    public ErrorVerse(String msg, Exception e) {
        super(Component.literal("%s: %s was thrown \n%s".formatted(msg, e.getClass().getSimpleName(), e.getMessage()))
            .withStyle(ChatFormatting.DARK_RED), 1);
    }

    public ErrorVerse(String msg) {
        super(Component.literal(msg).withStyle(ChatFormatting.DARK_RED), 1);
    }
}
