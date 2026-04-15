package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.components.LinePageComponent;
import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.init.CapabilityInit;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TimePeriodsIndexPage extends Page{

    public TimePeriodsIndexPage() {
        addComponent(new TitlePageComponent(Component.literal("Time Periods")).maxScale(1.5f),
                Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.2f)));


        addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, 0x558B7D6B),
                Inset.symmetric(LayoutValue.px(3), LayoutValue.px(7)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(2)));
        addAllDiscoveredTimePeriodPages();
    }

    private void addAllDiscoveredTimePeriodPages(){
        Minecraft.getInstance().player.getCapability(CapabilityInit.PLAYER_DISCOVERED_PREHISTORIC).ifPresent(cap -> {
            for (var period : cap.discoveredTimePeriods()) {
                addComponent(new TitlePageComponent(Component.literal("-").append(Component.literal(period.name()).plainCopy().withStyle(ChatFormatting.UNDERLINE)), 0x558B7D6B, false, false).maxScale(1f), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(6)), Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.05f)));
            }

        });
    }
}
