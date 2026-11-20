package com.deathmagneticlover16.boomstick;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class BoomstickNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            BoomstickMod.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private BoomstickNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, ReloadSoundPacket.class, ReloadSoundPacket::encode,
                ReloadSoundPacket::decode, ReloadSoundPacket::handle);
    }

    public static void startReloadSound(Entity source) {
        sendReloadSound(source, true);
    }

    public static void stopReloadSound(Entity source) {
        sendReloadSound(source, false);
    }

    private static void sendReloadSound(Entity source, boolean start) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> source),
                new ReloadSoundPacket(source.getId(), start));
    }

    private record ReloadSoundPacket(int entityId, boolean start) {
        private static void encode(ReloadSoundPacket packet, FriendlyByteBuf buffer) {
            buffer.writeVarInt(packet.entityId);
            buffer.writeBoolean(packet.start);
        }

        private static ReloadSoundPacket decode(FriendlyByteBuf buffer) {
            return new ReloadSoundPacket(buffer.readVarInt(), buffer.readBoolean());
        }

        private static void handle(ReloadSoundPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> ClientReloadSounds.handle(packet.entityId, packet.start)));
            context.setPacketHandled(true);
        }
    }
}
