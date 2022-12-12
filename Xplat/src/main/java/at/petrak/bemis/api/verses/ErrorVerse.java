package at.petrak.bemis.api.verses;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ErrorVerse extends TextVerse {
    @Nullable
    public Exception exn = null;

    public ErrorVerse(String msg, Exception e) {
        super(Component.literal("%s: %s was thrown \n%s".formatted(msg, e.getClass().getSimpleName(), e.getMessage()))
            .withStyle(ChatFormatting.DARK_RED), 1);
        this.exn = e;
    }

    public ErrorVerse(String msg) {
        super(Component.literal(msg).withStyle(ChatFormatting.DARK_RED), 1);
    }

    @Override
    public void onClick(int cornerX, int cornerY, double mouseX, double mouseY, int button) {
        if (this.exn != null) {
            this.exn.printStackTrace();
        }
    }
}
