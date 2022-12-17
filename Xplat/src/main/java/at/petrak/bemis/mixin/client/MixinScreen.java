package at.petrak.bemis.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface MixinScreen {
    @Invoker("renderTooltipInternal")
    void bemis$renderTooltipInternal(PoseStack ps, List<ClientTooltipComponent> components, int x, int y);
}
