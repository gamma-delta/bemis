package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.book.BemisVerse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

/**
 * A Verse that displays a paragraph of text, stripping newlines.
 */
public class TextVerse implements BemisVerse {
    protected final Component text;

    public TextVerse(String text) {
        this.text = Component.literal(text.replace("\n", " "));
    }

    public TextVerse(Component text) {
        this.text = text;
    }


    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        var seq = ctx.font().split(this.text, ctx.width());
        var vertKerning = ctx.font().lineHeight + 2;
        for (int i = 0; i < seq.size(); i++) {
            ctx.font().draw(ps, seq.get(i), 0f, i * vertKerning, 0xff_ffffff);
        }
        // add one line's worth of padding
        return (seq.size() + 1) * vertKerning;
    }
}
