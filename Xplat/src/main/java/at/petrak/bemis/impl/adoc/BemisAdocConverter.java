package at.petrak.bemis.impl.adoc;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.api.verses.TextVerse;
import net.minecraft.network.chat.Component;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
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
        if (node instanceof StructuralNode struct) {
            var subnodes = struct.getBlocks();
            var verses = new ArrayList<BemisVerse>();

            if (subnodes.isEmpty()) {
                // this is a leaf node with text
                var ctx = struct.getContext();
                if (struct.hasAttribute(BemisApi.BLOCK_MACRO_SENTINEL)) {
                    return (ConversionPage.BodyPart) struct.getAttribute(BemisApi.BLOCK_MACRO_SENTINEL);
                }

                // https://docs.asciidoctor.org/asciidoc/latest/blocks/
                if (ctx.equals("paragraph")) {
                    return new ConversionPage.BodyPart(new TextVerse((String) struct.getContent()));
                } else {
                    return new ConversionPage.BodyPart(new TextVerse("Error, unknown block context " + ctx));
                }
            } else {
                for (var subnode : subnodes) {
                    ConversionPage conved;
                    try {
                        conved = this.convert(subnode, null, opts);
                    } catch (Exception e) {
                        e.printStackTrace();
                        verses.add(new ErrorVerse("Error when converting an ADoc node `%s` to a verse:".formatted(subnode.getNodeName()), e));
                        continue;
                    }

                    if (conved instanceof ConversionPage.BodyPart bp) {
                        verses.addAll(bp.verses);
                    } else {
                        verses.add(new TextVerse("Error, node %s returned a Doc conversion page for some reason".formatted(subnode.getNodeName())));
                    }
                }
            }

            if (struct instanceof Document document) {
                // we were at the top level!
                // Gather information...
                final var title = document.getTitle();

                return new ConversionPage.Doc(new BemisPage(Component.literal(title), verses));
            } else {
                // this is at a non-top-level
                return new ConversionPage.BodyPart(verses);
            }
        } else {
            return new ConversionPage.BodyPart(new TextVerse(
                "Error: we don't know how to process non-StructuralNodes (found %s)".formatted(node.getClass().getSimpleName())));
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
