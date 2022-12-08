package at.petrak.bemis.common;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.client.ScreenBook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static at.petrak.bemis.api.BemisApi.modLoc;

public class ItemBook extends Item {
    public ItemBook(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide)
            Minecraft.getInstance().setScreen(new ScreenBook(BemisApi.get().getBook(modLoc("testbook"))));
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
