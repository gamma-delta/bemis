package at.petrak.bemis.common.msg;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.client.ScreenBook;
import at.petrak.paucal.api.msg.PaucalMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static at.petrak.bemis.api.BemisApi.modLoc;

public record MsgOpenBookS2C(ResourceLocation bookLoc) implements PaucalMessage {
    public static final ResourceLocation ID = modLoc("open");

    public void serialize(FriendlyByteBuf buf) {
        buf.writeUtf(this.bookLoc.toString());
    }

    public static MsgOpenBookS2C deserialize(FriendlyByteBuf buf) {
        var loc = ResourceLocation.tryParse(buf.readUtf());
        return new MsgOpenBookS2C(loc);
    }

    public static void handle(MsgOpenBookS2C msg) {
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                var book = BemisApi.get().getBook(msg.bookLoc);
                if (book != null) {
                    Minecraft.getInstance().setScreen(new ScreenBook(book));
                }
            }
        });
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}
