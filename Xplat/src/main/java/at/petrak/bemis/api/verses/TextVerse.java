package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisBook;
import at.petrak.bemis.api.BemisVerse;
import at.petrak.bemis.api.IBemisDrawCtx;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.w3c.dom.Node;

import java.io.Writer;

/**
 * A Verse that simply displays text.
 */
public class TextVerse implements BemisVerse {
    Component text;

    @Override
    public void load(Node node, BemisBook book, String thisPath) throws IllegalArgumentException {
        // TODO better error handling
        this.text = Component.literal(node.getTextContent());
    }

    @Override
    public int draw(PoseStack ps, IBemisDrawCtx ctx) {
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
