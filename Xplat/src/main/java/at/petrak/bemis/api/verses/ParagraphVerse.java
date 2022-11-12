package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.book.BemisVerse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Writer;

/**
 * A Verse that displays a paragraph of text, stripping newlines.
 */
public record ParagraphVerse(Component text) implements BemisVerse {
    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        var seq = ctx.font().split(this.text, ctx.width());
        var vertKerning = ctx.font().lineHeight + 2;
        for (int i = 0; i < seq.size(); i++) {
            ctx.font().draw(ps, seq.get(i), 0f, i * vertKerning, 0xff_ffffff);
        }
        return seq.size() * vertKerning;
    }

    @Override
    public void writeHTML(Writer htmlOut) {
        throw new NotImplementedException();
    }
}
