package at.petrak.bemis.core.adoc;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.api.verses.TextVerse;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.asciidoctor.ast.*;
import org.asciidoctor.converter.ConverterFor;
import org.asciidoctor.converter.StringConverter;
import org.asciidoctor.jruby.ast.impl.DocumentImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is a big fat lie! It doesn't convert to strings at all, it converts to BemisPages.
 * <p>
 * However, AsciiDoctor REALLY wants you outputting strings.
 * <p>
 * So we pull a little sneaky. We pretend we're outputting strings, when the only time we actually output a
 * string is when we're outputting plaintext. Then we smuggle the produced page out via an option.
 * <p>
 * Ughh.
 */
@ConverterFor("bemis")
public class BemisAdocConverter extends StringConverter {
    // https://stackoverflow.com/questions/5410066/what-are-the-default-font-sizes-in-pixels-for-the-html-heading-tags-h1-h2
    public static final float[] HEADER_SCALES = {2f, 1.5f, 1.17f, 1f, 0.83f, 0.67f};
    // if you are putting literal null bytes in your adoc page i am no longer your friend
    public static final String STUPID_AWFUL_COMPONENT_SENTINEL = "\0";


    private List<BemisVerse> processed = new ArrayList<>();
    private List<Component> wipPhraseSections = null;

    private Out out = null;

    public BemisAdocConverter(String backend, Map<String, Object> opts) {
        super(backend, opts);

        // auuuguhhh
        var document = (DocumentImpl) opts.get("document");
        var realOpts = document.getOptions();

        this.out = (Out) Objects.requireNonNull(realOpts.get(BemisApi.OUTPUT_SMUGGLING_SENTINEL));
    }


    // https://docs.asciidoctor.org/asciidoctorj/latest/extensions/ast-introduction/
    @Override
    public String convert(ContentNode node, String maybeTransform, Map<Object, Object> opts) {
        // Calling xyz.getContent forces the converter to walk the children nodes and add them to the internal list.
        // Thanks to the Adoc zulip for that tip
        if (node instanceof Document document) {
            assert this.processed.isEmpty();
            assert this.wipPhraseSections.isEmpty();

            this.addTitle(document);

            document.getContent();

            this.out.pageOut = new BemisPage(Component.literal(document.getDoctitle()), this.processed);
            return null;
        }

        try {
            if (node instanceof Section sect) {
                this.addTitle(sect);
                sect.getContent();
                return null;
            } else if (node instanceof Block block) {
                if (block.getContext().equals(BemisApi.VERSE_LITERAL_SENTINEL)) {
                    var preprocessed = (SneakyLiteralVerses) block.getAttribute(BemisApi.VERSE_LITERAL_SENTINEL);
                    this.processed.addAll(preprocessed.verses());
                    return null;
                }

                // Probably won't add a title, but it might if this is the preamble.
                this.addTitle(block);

                // https://docs.asciidoctor.org/asciidoc/latest/blocks/
                var tf = maybeTransform == null
                    ? node.getNodeName()
                    : maybeTransform;
                if (tf.equals("paragraph") || tf.equals("preamble")) {
                    this.wipPhraseSections = new ArrayList<>();

                    var content = (String) block.getContent();

                    if (!content.isBlank()) {
                        var toAdd = Component.empty();
                        var splitted = content.split(STUPID_AWFUL_COMPONENT_SENTINEL);
                        for (int i = 0; i < splitted.length; i++) {
                            var notComponent = splitted[i];
                            toAdd.append(Component.literal(BemisApi.get().unsubstituteAdoc(notComponent, true))
                                .withStyle(ChatFormatting.BLACK));
                            if (i < splitted.length - 1) {
                                var snuckOut = this.wipPhraseSections.get(i);
                                toAdd.append(snuckOut);
                            }
                        }

                        this.processed.add(new TextVerse(toAdd, 1f));
                    }

                    this.wipPhraseSections = null;
                } else if (tf.equals("pass")) {
                    this.processed.add(new TextVerse(Component.literal("Block passthrough %s\n%s"
                        .formatted(block.getStyle(), (String) block.getContent())), 1f));
                } else {
                    this.processed.add(new ErrorVerse(
                        "Error: tried to transform a Block with unknown transform `%s`".formatted(tf)));
                }
            } else if (node instanceof PhraseNode phrase) {
                var formatting = switch (phrase.getType()) {
                    case "strong" -> ChatFormatting.BOLD;
                    case "emphasis" -> ChatFormatting.ITALIC;
                    case "mark" -> ChatFormatting.UNDERLINE;
                    default -> null;
                };
                var comp = Component.literal(BemisApi.get().unsubstituteAdoc(phrase.getText(), true))
                    .withStyle(ChatFormatting.BLACK);
                if (formatting != null) {
                    comp.withStyle(formatting);
                }
                this.wipPhraseSections.add(comp);
                // ughhhhhhhhhhh
                return STUPID_AWFUL_COMPONENT_SENTINEL;
            } else {
                this.processed.add(new ErrorVerse(
                    "Error: found an AST node of type `%s` we don't know how to process".formatted(node.getClass().getSimpleName())));
            }
        } catch (Exception e) {
            this.processed.add(new ErrorVerse("Error when converting an AST node", e));
        }

        return null;
    }

    /**
     * If this node has a title, stick it on the list with the right size.
     */
    protected void addTitle(StructuralNode node) {
        if (node.getTitle() != null) {
            this.processed.add(new TextVerse(
                Component.literal(node.getTitle()).withStyle(ChatFormatting.BLACK),
                HEADER_SCALES[node.getLevel()]
            ));
        }
    }

    public static class Out {
        private BemisPage pageOut = null;

        public Out() {
        }

        public BemisPage getPage() {
            if (this.pageOut == null)
                throw new IllegalArgumentException("pass it into the converter you bonehead");
            return this.pageOut;
        }
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
    public void write(String output, OutputStream out) throws IOException {
        throw new IllegalStateException("no writing pages to out");
    }
}
