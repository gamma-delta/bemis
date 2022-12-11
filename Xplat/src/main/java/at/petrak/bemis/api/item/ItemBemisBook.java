package at.petrak.bemis.api.item;

import at.petrak.bemis.api.BemisApi;
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

/**
 * An item that opens the Bemis UI when right-clicked.
 * <p>
 * Bemis comes with a concrete implementation of this class. Right now it is hard-coded to always show the
 * test book, but in the future it will read the book location from its NBT.
 * <p>
 * Mod authors can subclass this and register their own item so the item belongs to their mod, if they like.
 */
public abstract class ItemBemisBook extends Item {
    public ItemBemisBook(Properties $$0) {
        super($$0);
    }

    /**
     * Get the ResourceLocation this book should open to.
     */
    @Nullable
    public abstract ResourceLocation getBookLoc(ItemStack stack);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (level.isClientSide) {
            var bookLoc = this.getBookLoc(itemInHand);
            if (bookLoc != null) {
                var book = BemisApi.get().getBook(bookLoc);
                if (book != null) {
                    Minecraft.getInstance().setScreen(new ScreenBook(book));
                }
            }
        }

        return InteractionResultHolder.success(itemInHand);
    }
}
