package at.petrak.bemis.core;

import at.petrak.bemis.api.BemisDrawCtx;
import at.petrak.bemis.client.ScreenBook;
import at.petrak.bemis.mixin.client.MixinScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BemisDrawCtxImpl implements BemisDrawCtx {
    public final Font font;
    public final int width;
    public int x, y;
    public final double mouseX, mouseY;
    public final ResourceLocation bookTexture;
    public final boolean isInit;

    protected final ScreenBook owner;

    public BemisDrawCtxImpl(ScreenBook owner, Font font, int width, int x, int y, double mouseX, double mouseY,
        ResourceLocation bookTexture,
        boolean isInit) {
        this.owner = owner;
        this.font = font;
        this.width = width;
        this.x = x;
        this.y = y;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.bookTexture = bookTexture;
        this.isInit = isInit;
    }

    @Override
    public Font font() {
        return font;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public double mouseX() {
        return mouseX;
    }

    @Override
    public double mouseY() {
        return mouseY;
    }

    @Override
    public ResourceLocation bookTexture() {
        return bookTexture;
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    @Override
    public void drawTooltip(PoseStack ps, List<ClientTooltipComponent> components, int x, int y) {
        // https://github.com/emilyploszaj/emi/blob/11b2e9eb8d6839e245a4341de479b792a2eaff87/src/main/java/dev/emi/emi/EmiRenderHelper.java#L93
        // should be mutable apparently
        var mutComps = new ArrayList<>(components);
        ((MixinScreen) this.owner).bemis$renderTooltipInternal(ps, mutComps, x, y);
    }

    @Override
    public List<ClientTooltipComponent> getStackTooltip(ItemStack stack) {
        return this.owner.getTooltipFromItem(stack)
            .stream()
            .map(Component::getVisualOrderText)
            .map(ClientTooltipComponent::create)
            .collect(Collectors.toList());
    }

    @Override
    public ScreenBook getOwningScreen() {
        return this.owner;
    }
}
