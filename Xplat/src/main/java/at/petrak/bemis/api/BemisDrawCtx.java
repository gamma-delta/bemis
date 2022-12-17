package at.petrak.bemis.api;

import at.petrak.bemis.client.ScreenBook;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Information about drawing.
 */
public interface BemisDrawCtx {
    Font font();

    int width();

    int x();

    int y();

    double mouseX();

    double mouseY();

    ResourceLocation bookTexture();

    /**
     * Return whether this is being "drawn" the first time to initialize and record the height.
     * <p>
     * If true, nothing will actually be drawn to the screen anywhere the player can see, so feel free to
     * return before anything is actually drawn.
     */
    boolean isInit();

    /**
     * Draw a tooltip. (This has to be on the {@link BemisDrawCtx} because it's a method on the screen.
     */
    void drawTooltip(PoseStack ps, List<ClientTooltipComponent> components, int x, int y);

    /**
     * Get the tooltip an itemstack displays.
     */
    List<ClientTooltipComponent> getStackTooltip(ItemStack stack);

    /**
     * If you need it for some reason (please be reasonable)
     */
    ScreenBook getOwningScreen();
}
