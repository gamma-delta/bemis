package at.petrak.bemis.api.verses.decal;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.api.BemisRenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;

public class BemisIngredientDecal extends BemisDecal {
    public final Ingredient ingredient;
    public final ItemStack[] items;

    public BemisIngredientDecal(int x, int y, Ingredient ingredient) {
        super(x, y, 16, 16);
        this.ingredient = ingredient;
        this.items = ingredient.getItems();
        // If i care to I can yoink the tag-detection code from EMI:
        // https://github.com/emilyploszaj/emi/blob/1.19.3/src/main/java/dev/emi/emi/api/stack/EmiIngredient.java#L92
    }

    @Override
    protected void draw(PoseStack ps, BemisDrawCtx ctx, float mx, float my, boolean isHovered) {
        if (isHovered) {
            // https://github.com/emilyploszaj/emi/blob/11b2e9eb8d6839e245a4341de479b792a2eaff87/src/main/java/dev/emi/emi/EmiRenderHelper.java#L101
            ps.pushPose();
            ps.translate(0, 0, 100);
            RenderSystem.colorMask(true, true, true, false);
            BemisRenderHelper.renderColorQuad(ps, 0, 0, this.width, this.height, 0x80_ffffff);
            RenderSystem.colorMask(true, true, true, true);
            ps.popPose();
        }

        if (this.items.length >= 1) {
            // https://github.com/VazkiiMods/Patchouli/blob/1850229d87b69f332827d6d5ac3dc07b15c37812/Xplat/src/main/java/vazkii/patchouli/client/book/gui/GuiBookEntry.java#L238
            ItemStack stack = this.items[0];

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
            if (this.items.length >= 2) {
                // https://github.com/emilyploszaj/emi/blob/1.19.3/src/main/java/dev/emi/emi/EmiRenderHelper.java#L137
                // y+12
                RenderSystem.disableDepthTest();
                BemisRenderHelper.drawTexture(ps, ctx.bookTexture(),
                    0, 12, 4, 4,
                    0, 220, 4, 4);
                RenderSystem.enableDepthTest();
            }

            if (isHovered) {
                // TODO: some kind of tag detection dammit am i just copying EMI lmao
                var tts = new ArrayList<>(ctx.getStackTooltip(this.items[0]));
                if (this.items.length >= 2) {
                    // TODO
                    tts.add(ClientTooltipComponent.create(
                        Component
                            .literal("and %d others".formatted(this.items.length - 1))
                            .getVisualOrderText()));
                }
//                RenderSystem.disableDepthTest();
                ps.pushPose();
                ps.translate(0, 0, 100);
                ctx.drawTooltip(ps, tts, (int) mx, (int) my);
                ps.popPose();
//                RenderSystem.enableDepthTest();
            }
        }
    }
}
