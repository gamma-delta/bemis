package at.petrak.bemis.impl.adoc;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.api.verses.TextVerse;
import net.minecraft.network.chat.Component;
import org.asciidoctor.ast.*;
import org.asciidoctor.converter.AbstractConverter;
import org.asciidoctor.converter.ConverterFor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConverterFor("bemis")
public class BemisAdocConverter extends AbstractConverter<ConversionPage> {
    public BemisAdocConverter(String backend, Map<String, Object> opts) {
        super(backend, opts);
    }

    // https://stackoverflow.com/questions/5410066/what-are-the-default-font-sizes-in-pixels-for-the-html-heading-tags-h1-h2
    private final float[] HEADER_SCALES = {2f, 1.5f, 1.17f, 1f, 0.83f, 0.67f};


    // https://docs.asciidoctor.org/asciidoctorj/latest/extensions/ast-introduction/
    @Override
    public ConversionPage convert(ContentNode node, String maybeTransform, Map<Object, Object> opts) {
        if (node instanceof Document document) {
            var verses = this.convertChildren(document, opts);
            // we were at the top level!
            // Gather information...
            final var title = document.getTitle();
            return new ConversionPage.Doc(new BemisPage(Component.literal(title), verses));
        } else if (node instanceof Section sect) {
            var subverses = this.convertChildren(sect, opts);
            return new ConversionPage.BodyPart(subverses);
        } else if (node instanceof Block block) {
            if (block.hasAttribute(BemisApi.BLOCK_MACRO_SENTINEL)) {
                return (ConversionPage.BodyPart) block.getAttribute(BemisApi.BLOCK_MACRO_SENTINEL);
            }

            // https://docs.asciidoctor.org/asciidoc/latest/blocks/
            var tf = maybeTransform == null
                ? node.getNodeName()
                : maybeTransform;
            if (tf.equals("paragraph")) {
                var content = (String) block.getContent();
                return new ConversionPage.BodyPart(new TextVerse(content));
            } else if (tf.equals("preamble")) {
                // Weird special case: usually blocks don't have their own children, but the preamble does!
                var subverses = this.convertChildren(block, opts);
                return new ConversionPage.BodyPart(subverses);
            } else {
                return new ConversionPage.BodyPart(new ErrorVerse(
                    "Error: tried to transform a Block with unknown transform `%s`".formatted(tf)));
            }
        } else {
            return new ConversionPage.BodyPart(new ErrorVerse(
                "Error: found an AST node of type `%s` we don't know how to process".formatted(node.getClass().getSimpleName())));
        }
    }

    protected List<BemisVerse> convertChildren(StructuralNode parent, Map<Object, Object> opts) {
        var children = parent.getBlocks();
        var verses = new ArrayList<BemisVerse>();
        if (parent.getTitle() != null) {
            verses.add(new TextVerse(Component.literal(parent.getTitle()), HEADER_SCALES[parent.getLevel()]));
        }
        for (var subnode : children) {
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

        return verses;
    }

    //

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
