package at.petrak.bemis.api.book;

import java.util.List;

public record BemisPage(String title, List<BemisVerse> verses) {
}
