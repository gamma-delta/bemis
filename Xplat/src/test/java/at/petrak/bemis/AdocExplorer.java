package at.petrak.bemis;

import at.petrak.bemis.adoc.BemisAdocConverter;
import at.petrak.bemis.adoc.ConversionPage;
import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.verses.TextVerse;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class AdocExplorer {
    private static final String DOCUMENT = """
        = A document
                
        Hello world!
                
        Lorem ipsum dolor sic amet.
                
        testmacro::foobar[]
        """;

    @Test
    public void test() {
        var asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaConverterRegistry().register(BemisAdocConverter.class);
        asciidoctor.javaExtensionRegistry().blockMacro(TestMacro.class);

        var page = asciidoctor.convert(DOCUMENT,
            Options.builder().backend("bemis").toFile(false).build(),
            ConversionPage.class);
        System.out.println(page);
    }

    @Name("testmacro")
    public static class TestMacro extends BlockMacroProcessor {
        @Override
        public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
            var verse = new TextVerse("testing macro! through the macro!");
            return BemisApi.get().makeVerseMacroNode(this, parent, List.of(verse));
        }
    }
}

