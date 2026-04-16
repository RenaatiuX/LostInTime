package com.ren.lostintime.common.init;

import com.ren.lostintime.common.entity.util.LostInTimeBookDescription;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityInit {

    public static final Capability<LostInTimeBookDescription> ENTITY_DESCRIPTION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
}
