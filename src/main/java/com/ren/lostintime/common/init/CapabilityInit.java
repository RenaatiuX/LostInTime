package com.ren.lostintime.common.init;

import com.ren.lostintime.common.entity.util.LostInTimeBookDescription;
import com.ren.lostintime.common.entity.util.PlayerDiscoveredPrehistoric;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.energy.IEnergyStorage;

public class CapabilityInit {

    public static final Capability<LostInTimeBookDescription> ENTITY_DESCRIPTION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<PlayerDiscoveredPrehistoric> PLAYER_DISCOVERED_PREHISTORIC = CapabilityManager.get(new CapabilityToken<>(){});
}
