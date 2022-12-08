package at.petrak.bemis.fabric;

import at.petrak.bemis.xplat.Xplat;
import at.petrak.paucal.api.msg.PaucalMessage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricXplatImpl implements Xplat {
    @Override
    public void sendPacketToPlayer(ServerPlayer target, PaucalMessage packet) {
        ServerPlayNetworking.send(target, packet.getFabricId(), packet.toBuf());
    }
}
