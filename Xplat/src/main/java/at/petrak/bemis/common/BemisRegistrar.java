package at.petrak.bemis.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static at.petrak.bemis.api.BemisApi.modLoc;

public class BemisRegistrar {
    public static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();


    public static final ItemBook BOOK = item("book",
        new ItemBook(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

    private static <T extends Item> T item(String path, T item) {
        var id = modLoc(path);
        var old = ITEMS.put(id, item);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return item;
    }

    public static void registerItems(BiConsumer<ResourceLocation, Item> consumer) {
        ITEMS.forEach(consumer);
    }
}
