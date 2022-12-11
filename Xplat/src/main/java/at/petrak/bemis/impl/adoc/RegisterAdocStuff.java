package at.petrak.bemis.impl.adoc;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.verses.ScreenPosVerse;
import at.petrak.bemis.impl.BemisBookRegistry;

public class RegisterAdocStuff {
    public static void register() {
        var asciidoc = BemisBookRegistry.ASCIIDOCTOR;
        asciidoc.javaConverterRegistry().register(BemisAdocConverter.class);

        var exts = BemisApi.get().getJavaExtensionRegistry();
        exts.blockMacro(ScreenPosVerse.Macro.class);
    }
}
