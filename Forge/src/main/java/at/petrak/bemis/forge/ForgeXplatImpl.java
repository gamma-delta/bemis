package at.petrak.bemis.forge;

import at.petrak.bemis.forge.msg.ForgePacketHandler;
import at.petrak.bemis.xplat.Xplat;
import at.petrak.paucal.api.msg.PaucalMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ForgeXplatImpl implements Xplat {
    @Override
    public void sendPacketToPlayer(ServerPlayer target, PaucalMessage packet) {
        ForgePacketHandler.getNetwork().send(PacketDistributor.PLAYER.with(() -> target), packet);
    }
}
