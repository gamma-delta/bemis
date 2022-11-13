package at.petrak.bemis.common;

import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.book.BemisVerseType;
import at.petrak.bemis.api.verses.ParagraphVerse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;

import static at.petrak.bemis.api.BemisApi.modLoc;

public class BemisRegistrar {
    public static final Map<ResourceLocation, BemisVerseType<?>> VERSE_TYPES = new LinkedHashMap<>();
    public static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();

    public static final BemisVerseType<ParagraphVerse> VERSE_PARAGRAPH = verseType("paragraph", ParagraphVerse.TYPE);

    public static final ItemBook BOOK = item("book",
        new ItemBook(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

    private static <V extends BemisVerse, T extends BemisVerseType<V>> T verseType(String path, T item) {
        var id = modLoc(path);
        var old = VERSE_TYPES.put(id, item);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return item;
    }

    private static <T extends Item> T item(String path, T item) {
        var id = modLoc(path);
        var old = ITEMS.put(id, item);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return item;
    }
}
