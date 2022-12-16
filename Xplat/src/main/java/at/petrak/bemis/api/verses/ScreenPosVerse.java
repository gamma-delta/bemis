package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.decal.BemisDecalHolder;
import at.petrak.bemis.api.verses.decal.MousePosDecal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;

import java.util.List;
import java.util.Map;

/**
 * For debugging mostly, displays the screen position and other stuff.
 */
public class ScreenPosVerse implements BemisVerse {
    public int clickCount = 0;

    public ScreenPosVerse() {
    }

    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        var text = "%d, %d ; %.2f %.2f ; %dx".formatted(ctx.x(), ctx.y(), ctx.mouseX(), ctx.mouseY(), this.clickCount);
        ctx.font().draw(ps, Component.literal(text).withStyle(ChatFormatting.DARK_PURPLE), 0f, 0f, 0xff_ffffff);

        return Math.round((ctx.font().lineHeight + 2) * 2);
    }

    @Override
    public void onClick(int cornerX, int cornerY, double mouseX, double mouseY, int button) {
        this.clickCount += 1;
    }

    @Name("screenpos")
    public static final class Macro extends BlockMacroProcessor {
        @Override
        public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
//            return BemisApi.get().makeVerseLiteralNode(this, parent, List.of(new ScreenPosVerse()));

            var decals = new BemisDecalHolder(64, 16);
            decals.addDecal(new MousePosDecal(16, 16));
            return BemisApi.get().makeVerseLiteralNode(this, parent, List.of(new CenteredDecalVerse(decals)));
        }
    }
}
