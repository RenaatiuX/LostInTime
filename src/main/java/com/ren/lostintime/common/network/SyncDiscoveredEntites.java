package com.ren.lostintime.common.network;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import com.ren.lostintime.common.init.CapabilityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDiscoveredEntites {


    protected int ownerId;
    protected CompoundTag tag;

    public SyncDiscoveredEntites(int ownerId, CompoundTag tag) {
        this.tag = tag;
        this.ownerId = ownerId;
    }
    public SyncDiscoveredEntites(FriendlyByteBuf buf){
        this(buf.readInt(), buf.readNbt());
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(ownerId);
        buf.writeNbt(tag);
    }


    public void handle(Supplier<NetworkEvent.Context> ctxSupplier){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var entity = Minecraft.getInstance().level.getEntity(ownerId);
            if (entity != null){
                entity.getCapability(CapabilityInit.PLAYER_DISCOVERED_PREHISTORIC).ifPresent(d -> d.deserializeNBT(tag));
            }else {
                LostInTime.LOGGER.warn("no entity with id {} was found even tho a packet was send to the client for discovered entity syncs", ownerId);
            }
        });
        ctxSupplier.get().setPacketHandled(true);
    }
}
