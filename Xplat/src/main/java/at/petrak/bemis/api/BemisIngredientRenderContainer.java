package at.petrak.bemis.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for rendering ingredients
 */
public class BemisIngredientRenderContainer {
    private final int x;
    private final int y;
    private final float scale;

    private final List<Entry> entries = new ArrayList<>();

    /**
     * @param x     The relative x position, where 0 is the left side of the verse.
     * @param y     The relative y position, where 0 is the top of the verse
     * @param scale The scale of the drawn ingredients, and their location relative to the origin, will be scaled
     *              by this much.
     */
    public BemisIngredientRenderContainer(int x, int y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    /**
     * Add an ingredient to the renderer.
     */
    public void addIngredient(int dx, int dy, Ingredient ingredient) {
        this.entries.add(new Entry(dx, dy, ingredient, ingredient.getItems()));
    }

    public void draw(PoseStack ps, BemisDrawCtx ctx) {
        ps.pushPose();

        ps.translate(this.x, this.y, 1);
        ps.scale(this.scale, this.scale, 1);

        for (var entry : this.entries) {
            ps.pushPose();
            ps.translate(entry.dx, entry.dy, 0);

            if (entry.items.length >= 1) {
                // https://github.com/VazkiiMods/Patchouli/blob/1850229d87b69f332827d6d5ac3dc07b15c37812/Xplat/src/main/java/vazkii/patchouli/client/book/gui/GuiBookEntry.java#L238
                ItemStack stack = entry.items[0];

                if (!stack.isEmpty()) {
                    BemisRenderHelper.transferMsToGl(ps, () -> {
                        var ir = Minecraft.getInstance().getItemRenderer();
                        ir.renderAndDecorateItem(stack, 0, 0);
                        ir.renderGuiItemDecorations(ctx.font(), stack, 0, 0);
                    });
                }

                // Take a page out of EMI's book
                // If multiple items are allowed for an ingr, don't flash them all, just display an asterisk
                // TODO: hovers, etc
                if (entry.items.length >= 2) {
                    // https://github.com/emilyploszaj/emi/blob/1.19.3/src/main/java/dev/emi/emi/EmiRenderHelper.java#L137
                    // y+12
                    RenderSystem.disableDepthTest();
                    BemisRenderHelper.drawTexture(ps, ctx.bookTexture(),
                        0, 12, 4, 4,
                        0, 220, 4, 4);
                    RenderSystem.enableDepthTest();
                }
            }

            ps.popPose();
        }

        ps.popPose();
    }

    private record Entry(int dx, int dy, Ingredient ingredient, ItemStack[] items) {
    }
}
