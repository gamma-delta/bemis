package at.petrak.bemis.impl.adoc;

import at.petrak.bemis.api.book.BemisPage;
import at.petrak.bemis.api.book.BemisVerse;

import java.util.List;

/**
 * Page skeleton that {@link BemisAdocConverter} converts to, for better invariants.
 */
abstract public sealed class ConversionPage {
    /**
     * Whole document body. This is what the converter returns.
     */
    public static final class Doc extends ConversionPage {
        public final BemisPage out;

        public Doc(BemisPage out) {
            this.out = out;
        }
    }

    /**
     * Segment of the page body, containing verses.
     */
    public static final class BodyPart extends ConversionPage {
        public final List<BemisVerse> verses;

        public BodyPart(List<BemisVerse> verses) {
            this.verses = verses;
        }

        public BodyPart(BemisVerse... verses) {
            this.verses = List.of(verses);
        }
    }
}
