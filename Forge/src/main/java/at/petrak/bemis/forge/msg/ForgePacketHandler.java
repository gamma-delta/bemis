package at.petrak.bemis.forge.msg;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import static at.petrak.bemis.api.BemisApi.modLoc;

public class ForgePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        modLoc("main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getNetwork() {
        return NETWORK;
    }

    public static void init() {
        int messageIdx = 0;

//        NETWORK.registerMessage(messageIdx++, MsgOpenBookS2C.class)
    }
}
