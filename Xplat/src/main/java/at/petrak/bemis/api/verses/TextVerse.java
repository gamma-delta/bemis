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
    protected final float scale;

    public TextVerse(Component text, float scale) {
        this.text = text;
        this.scale = scale;
    }

    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        float reasonableScale = this.scale * 0.75f;

        var seq = ctx.font().split(this.text, Math.round(ctx.width() / reasonableScale));
        var vertKerning = ctx.font().lineHeight + 2;
        ps.pushPose();


        ps.scale(reasonableScale, reasonableScale, 1f);
        for (int i = 0; i < seq.size(); i++) {
            ctx.font().draw(ps, seq.get(i), 0f, vertKerning * i, 0xff_ffffff);
        }
        ps.popPose();
        return Math.round(seq.size() * vertKerning * reasonableScale + DEFAULT_VERTICAL_PADDING);
    }
}
