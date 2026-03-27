package com.ren.lostintime.common.entity.util;

import com.ren.lostintime.common.init.CapabilityInit;
import com.ren.lostintime.common.init.NetworkInit;
import com.ren.lostintime.common.network.SyncDiscoveredEntites;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class PlayerDiscoveredPrehistoricImpl implements PlayerDiscoveredPrehistoric {

    protected Set<TimePeriod> periods = new HashSet<>();
    protected Map<EntityType<?>, LostInTimeBookDescription> descriptions = new HashMap<>();

    protected final Entity owner;

    public PlayerDiscoveredPrehistoricImpl(Entity owner) {
        this.owner = owner;
    }


    @Override
    public Set<TimePeriod> discoveredTimePeriods() {
        return new HashSet<>(periods);
    }

    @Override
    public void discoverTimePeriod(TimePeriod period) {
        periods.add(period);
        sendUpdate();
    }

    @Override
    public void discoverEntity(Entity entity) {
        if (canDiscover(entity)) {
            var descr = entity.getCapability(CapabilityInit.ENTITY_DESCRIPTION_CAPABILITY).resolve().orElseThrow();
            descriptions.put(entity.getType(), descr);
            //this will already send a full update
            discoverTimePeriod(descr.getPeriod());
        }
    }

    @Override
    public Set<LostInTimeBookDescription> discoveredEntities() {
        return new HashSet<>(descriptions.values());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag periodListTag = new ListTag();
        for (TimePeriod period : periods) {
            periodListTag.add(IntTag.valueOf(period.ordinal()));
        }
        tag.put("periods", periodListTag);

        ListTag entityListTag = new ListTag();

        for (EntityType<?> description : descriptions.keySet()) {
            entityListTag.add(StringTag.valueOf(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(description)).toString()));
        }
        tag.put("discoveredEntities", entityListTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("periods", 9)) {
            var listTag = nbt.getList("periods", 3);
            for (int i = 0; i < listTag.size(); i++) {
                int ordinal = listTag.getInt(i);
                periods.add(TimePeriod.values()[ordinal]);
            }
        }
        if (nbt.contains("discoveredEntities", 9)) {
            var listTag = nbt.getList("discoveredEntities", 8);
            for (int i = 0; i < listTag.size(); i++) {
                var entityType = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.parse(listTag.getString(i)));
                if (entityType != null) {
                    var entity = entityType.create(owner.level());
                    descriptions.put(entityType, entity.getCapability(CapabilityInit.ENTITY_DESCRIPTION_CAPABILITY).resolve().orElseThrow());
                }
            }

        }
    }

    protected void sendUpdate(){
        NetworkInit.DISCOVERED_ENTITIES_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> owner), new SyncDiscoveredEntites(owner.getId(), serializeNBT()));
    }

    @Override
    public void reset() {
        periods.clear();
        descriptions.clear();
    }
}
