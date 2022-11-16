package at.petrak.bemis.adoc;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.api.verses.TextVerse;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.Document;
import org.asciidoctor.converter.AbstractConverter;
import org.asciidoctor.converter.ConverterFor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

@ConverterFor("bemis")
public class BemisAdocConverter extends AbstractConverter<ConversionPage> {
    public BemisAdocConverter(String backend, Map<String, Object> opts) {
        super(backend, opts);
    }

    @Override
    public ConversionPage convert(ContentNode node, String maybeTransform, Map<Object, Object> opts) {
        // The automatic "forward via getContent" seems to only want to do strings.
        if (node instanceof Document document) {
            var subnodes = document.getBlocks();
            var verses = new ArrayList<BemisVerse>();

            for (var subnode : subnodes) {
                ConversionPage conved;
                try {
                    conved = this.convert(subnode, null, opts);
                } catch (Exception e) {
                    verses.add(new ErrorVerse("error in conversion: " + e.getMessage()));
                    continue;
                }

                if (conved instanceof ConversionPage.BodyPart bp) {
                    verses.addAll(bp.verses);
                } else {
                    verses.add(
                        new ErrorVerse("node %s returned a Doc conversion page".formatted(subnode.getNodeName())));
                }
            }

            // Gather information...
            final var title = document.getTitle();

            return new ConversionPage.Doc(new BemisPage(title, verses));
        } else if (node instanceof Block block) {
            var kind = block.getNodeName();
            try {
                if (block.hasAttribute(BemisApi.BLOCK_MACRO_SENTINEL)) {
                    return (ConversionPage.BodyPart) block.getAttribute(BemisApi.BLOCK_MACRO_SENTINEL);
                }

                if (kind.equals("paragraph")) {
                    return new ConversionPage.BodyPart(new TextVerse((String) block.getContent()));
                } else {
                    return new ConversionPage.BodyPart(new ErrorVerse("unknown block context " + kind));
                }
            } catch (Exception e) {
                return new ConversionPage.BodyPart(new ErrorVerse(e.getMessage()));
            }
        } else {
            return new ConversionPage.BodyPart(
                new ErrorVerse("tried to convert node of bad type " + node.getClass().getCanonicalName()));
        }
    }

    @Override
    public void setOutfileSuffix(String outfilesuffix) {
        // NO OP
    }

    @Override
    public String getOutfileSuffix() {
        return "DO NOT EXPORT TO FILE";
    }

    @Override
    public void write(ConversionPage output, OutputStream out) throws IOException {
        throw new IllegalStateException("no writing pages to out");
    }
}
