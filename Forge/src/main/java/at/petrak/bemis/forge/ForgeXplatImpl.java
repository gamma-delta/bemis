package at.petrak.bemis.forge;

import at.petrak.bemis.api.book.BemisVerseType;
import at.petrak.bemis.xplat.Xplat;
import net.minecraft.core.Registry;
import org.apache.commons.lang3.NotImplementedException;

public class ForgeXplatImpl implements Xplat {
    @Override
    public Registry<BemisVerseType<?>> getVerseTypeRegistry() {
        throw new NotImplementedException();
    }
}
