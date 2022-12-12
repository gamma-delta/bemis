package at.petrak.bemis.api.verses;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.BemisIngredientRenderContainer;
import at.petrak.bemis.api.BemisRenderHelper;
import at.petrak.bemis.api.book.BemisVerse;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.PositionalAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CraftingVerse implements BemisVerse {

    public static final int GRAPHIC_WIDTH = 102;
    public static final int GRAPHIC_HEIGHT = 64;

    public static final float GRAPHIC_SCALE = 2f;

    @Nullable
    public final ResourceLocation recipeLoc;
    @Nullable
    public final CraftingRecipe recipe;
    @Nullable
    public BemisIngredientRenderContainer items;

    public CraftingVerse(ResourceLocation recipeLoc) {
        this.recipeLoc = recipeLoc;
        if (this.recipeLoc != null) {
            var reciMan = Minecraft.getInstance().level.getRecipeManager();
            this.recipe = (CraftingRecipe) reciMan.byKey(this.recipeLoc)
                .filter(r -> r.getType() == RecipeType.CRAFTING)
                .orElse(null);
        } else {
            this.recipe = null;
        }
    }

    @Override
    public int draw(PoseStack ps, BemisDrawCtx ctx) {
        // At most this is allowed to take up 0.8 of the width of the page
        // If it's less than that, than we start shrinking the height to match.
        float scale = 1f;
        var maxWidth = ctx.width() * 0.8f;
        if (maxWidth < GRAPHIC_WIDTH * GRAPHIC_SCALE) {
            scale *= maxWidth / GRAPHIC_WIDTH * GRAPHIC_WIDTH;
        }

        if (ctx.isInit() && this.recipe != null) {
            this.items = new BemisIngredientRenderContainer(
                (ctx.width() / 2) -
                    (int) (GRAPHIC_WIDTH * GRAPHIC_SCALE * scale / 2f),
                0, scale * GRAPHIC_SCALE
            );
            // https://github.com/emilyploszaj/emi/blob/1.19.3/src/main/java/dev/emi/emi/api/recipe/EmiCraftingRecipe.java#L74
            var ingrs = this.recipe.getIngredients();
            int rw = 3, rh = 3;
            if (this.recipe instanceof ShapedRecipe shaped) {
                rw = shaped.getWidth();
                rh = shaped.getHeight();
            }
            for (int i = 0; i < ingrs.size(); i++) {
                int x = i % rw;
                int y = i / rw;
                this.items.addIngredient(5 + x * (16 + 3), 5 + y * (16 + 3), ingrs.get(i));
            }
            this.items.addIngredient(81, 24, Ingredient.of(recipe.getResultItem()));
        }

        if (this.items != null) {
            this.items.draw(ps, ctx);
        }

        ps.pushPose();
        ps.translate(ctx.width() / 2f, 0, 1);

        ps.scale(scale, scale, 1);

        ps.translate(-GRAPHIC_WIDTH * GRAPHIC_SCALE / 2f, 0, 0);
        ps.scale(GRAPHIC_SCALE, GRAPHIC_SCALE, 1);
        RenderSystem.enableBlend();
        BemisRenderHelper.drawTexture(ps, ctx.bookTexture(),
            0, 0, GRAPHIC_WIDTH, GRAPHIC_HEIGHT,
            0, 144, GRAPHIC_WIDTH, GRAPHIC_HEIGHT);

        if (this.recipe instanceof ShapelessRecipe) {
            BemisRenderHelper.drawTexture(ps, ctx.bookTexture(),
                64, 2, 11, 11,
                0, 208, 11, 11);
        }

//        ctx.font().draw(ps, String.valueOf(this.recipeLoc), 0, 0, -1);

        ps.popPose();

        return (int) (GRAPHIC_HEIGHT * scale * GRAPHIC_SCALE) + 10;
    }

    @Name("bemis!crafting")
    @PositionalAttributes({"recipe"})
    public static final class Macro extends BlockMacroProcessor {
        @Override
        public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
            var recipeLoc = ResourceLocation.tryParse((String) attributes.get("recipe"));
            var verse = new CraftingVerse(recipeLoc);
            return BemisApi.get().makeVerseLiteralNode(this, parent, List.of(verse));
        }
    }
}
