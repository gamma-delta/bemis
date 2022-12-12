package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisBook;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.impl.adoc.ConversionPage;
import at.petrak.bemis.impl.adoc.SneakyLiteralVerses;
import net.minecraft.resources.ResourceLocation;
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
}
