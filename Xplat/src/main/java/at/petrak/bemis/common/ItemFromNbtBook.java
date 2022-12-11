package at.petrak.bemis.common;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.item.ItemBemisBook;
import at.petrak.bemis.client.ScreenBook;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static at.petrak.bemis.api.BemisApi.modLoc;

/**
 * In the future will read the book location from NBT, right now it's hardcoded
 */
public class ItemFromNbtBook extends ItemBemisBook {
    public ItemFromNbtBook(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable ResourceLocation getBookLoc(ItemStack stack) {
        return modLoc("testbook");
    }
}
