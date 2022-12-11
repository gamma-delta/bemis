package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.book.BemisVerse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;

import java.util.List;
import java.util.Map;

/**
 * For debugging mostly, displays the screen position.
 */
public class ScreenPosVerse implements BemisVerse {
    public ScreenPosVerse() {
    }

    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        var text = "%d, %d".formatted(ctx.x, ctx.y);
        ctx.font.draw(ps, Component.literal(text).withStyle(ChatFormatting.GRAY), 0f, 0f, 0xff_ffffff);

        return Math.round((ctx.font.lineHeight + 2) * 2);
    }

    @Name("screenpos")
    public static final class Macro extends BlockMacroProcessor {
        @Override
        public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
            return BemisApi.get().makeVerseMacroNode(this, parent, List.of(new ScreenPosVerse()));
        }
    }
}
