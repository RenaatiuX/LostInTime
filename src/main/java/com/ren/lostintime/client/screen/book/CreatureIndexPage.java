package com.ren.lostintime.client.screen.book;

import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.init.CapabilityInit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class CreatureIndexPage extends Page{

    public CreatureIndexPage() {
        addComponent(new TitlePageComponent(Component.literal("Creature Index")).maxScale(1.5f).addUnderline(2, 0x558B7D6B, Inset.symmetric(LayoutValue.px(3), LayoutValue.px(7))), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)), Dimension.of(DimensionValue.fill(), DimensionValue.none()));
        addAllDiscoveredTimePeriodPages();
    }

    private void addAllDiscoveredTimePeriodPages(){
        Minecraft.getInstance().player.getCapability(CapabilityInit.PLAYER_DISCOVERED_PREHISTORIC).ifPresent(cap -> {
            for (var entity : cap.discoveredEntities()) {
                addComponent(new TitlePageComponent(Component.literal("-").append(entity.getDisplayName().plainCopy().withStyle(ChatFormatting.UNDERLINE)), 0x558B7D6B, false, false).maxScale(1f), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(6)), Dimension.of(DimensionValue.fill(), DimensionValue.none()));
            }

        });
    }
}
