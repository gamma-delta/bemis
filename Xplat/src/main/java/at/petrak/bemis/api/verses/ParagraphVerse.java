package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.book.BemisBookConfig;
import at.petrak.bemis.api.book.BemisIndex;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.book.BemisVerseType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.NotImplementedException;
import org.w3c.dom.Node;

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

    public static final BemisVerseType<ParagraphVerse> TYPE = new BemisVerseType<>() {
        @Override
        public ParagraphVerse load(Node node, BemisBookConfig config, BemisIndex<ResourceLocation> index,
            String path) throws IllegalArgumentException {
            // TODO: span interpolations
            var text = node.getTextContent();
            return new ParagraphVerse(Component.literal(text));
        }
    };
}
