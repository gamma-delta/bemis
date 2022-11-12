package at.petrak.bemis.impl;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisVerseType;
import at.petrak.bemis.xplat.Xplat;
import net.minecraft.core.Registry;

public class BemisApiImpl implements BemisApi.IBemisApi {
    @Override
    public Registry<BemisVerseType<?>> getVerseTypeRegistry() {
        return Xplat.get().getVerseTypeRegistry();
    }
}
