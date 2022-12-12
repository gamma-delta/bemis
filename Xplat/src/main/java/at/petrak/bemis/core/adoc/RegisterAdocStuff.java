package at.petrak.bemis.core.adoc;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.verses.CraftingVerse;
import at.petrak.bemis.api.verses.ScreenPosVerse;
import at.petrak.bemis.core.impl.BemisBookRegistry;
import org.asciidoctor.jruby.internal.JRubyAsciidoctor;
import org.jruby.RubyRegexp;

public class RegisterAdocStuff {
    public static void register() {
        reachMyFunnyLittleFingersIntoAdocAndDoThingsIProbablyShouldnt();

        BemisBookRegistry.ASCIIDOCTOR.javaConverterRegistry().register(BemisAdocConverter.class);

        var exts = BemisApi.get().getJavaExtensionRegistry();
        exts.blockMacro(ScreenPosVerse.Macro.class);
        exts.blockMacro(CraftingVerse.Macro.class);
    }

    private static void reachMyFunnyLittleFingersIntoAdocAndDoThingsIProbablyShouldnt() {
        var asciidoc = (JRubyAsciidoctor) BemisBookRegistry.ASCIIDOCTOR;

        // i am so so sorry
        var ruby = asciidoc.getRubyRuntime();
        var adocModule = ruby.getModule("Asciidoctor");
        // if you're complaining that this is fragile, yes! it is! all weakly typed languages are this fragile!
        // anyways we do need some kind of sigil character for the namespace, so insert a `!`
        // https://github.com/asciidoctor/asciidoctor/blob/main/lib/asciidoctor/rx.rb#L412
        var blockMacroRegex = (RubyRegexp) adocModule.getConstant("CustomBlockMacroRx");
        adocModule.setConstant("CustomBlockMacroRx", RubyRegexp.newRegexp(ruby,
            "^(\\p{Word}[\\p{Word}!-]*)::(|\\S|\\S.*?\\S)\\[(.+)?\\]$",
            blockMacroRegex.getOptions()));
    }
}
