package at.petrak.bemis.core.impl;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.core.adoc.BemisAdocConverter;
import at.petrak.bemis.core.adoc.SneakyLiteralVerses;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.asciidoctor.Options;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static at.petrak.bemis.api.BemisApi.VERSE_LITERAL_SENTINEL;

public class BemisApiImpl implements BemisApi.IBemisApi {
    @Override
    public Block makeVerseLiteralNode(BlockMacroProcessor self, StructuralNode parent, List<BemisVerse> verses) {
        return self.createBlock(parent, VERSE_LITERAL_SENTINEL, "",
            Map.of(VERSE_LITERAL_SENTINEL, new SneakyLiteralVerses(verses)));
    }

    @Override
    public JavaExtensionRegistry getJavaExtensionRegistry() {
        return BemisBookRegistry.ASCIIDOCTOR.javaExtensionRegistry();
    }

    @Override
    public @Nullable BemisBook getBook(ResourceLocation bookLoc) {
        var skeleton = BemisBookRegistry.getBook(bookLoc);
        if (skeleton == null)
            return null;

        return new BemisBookImpl(bookLoc, skeleton.cfg(), skeleton.index().map(LazyPage::new));
    }

    @Override
    public BemisPage loadString(String adocSrc) {
        // if anyone knows of a better way of doing this,
        // god please, I am dying to know
        var out = new BemisAdocConverter.Out();
        BemisBookRegistry.ASCIIDOCTOR.convert(adocSrc,
            Options.builder()
                .backend("bemis")
                .toFile(false)
                .option(BemisApi.OUTPUT_SMUGGLING_SENTINEL, out)
                .build(),
            BemisPage.class);
        return out.getPage();
    }

    @Override
    public <C extends Container, T extends Recipe<C>> @Nullable T getRecipe(RecipeManager recipes,
        ResourceLocation loc, RecipeType<T> type) {
        Recipe<?> recipe = recipes.byKey(loc)
            .filter(r -> r.getType() == type)
            .orElse(null);
        return (T) recipe;
    }
}
