package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.components.LinePageComponent;
import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.entity.util.TimePeriod;
import com.ren.lostintime.common.init.CapabilityInit;
import com.ren.lostintime.common.item.PrehistoricBookItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TimePeriodsIndexPage extends Page {

    public TimePeriodsIndexPage(ItemStack book) {
        addComponent(new TitlePageComponent(Component.literal("Time Periods")).maxScale(1.5f),
                Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.2f)));


        addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, 0x558B7D6B),
                Inset.symmetric(LayoutValue.px(3), LayoutValue.px(7)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(2)));
        addAllDiscoveredTimePeriodPages(book);
    }

    private void addAllDiscoveredTimePeriodPages(ItemStack book) {
        for (TimePeriod p : PrehistoricBookItem.discoveredTimePeriods(book)) {
            addComponent(new TitlePageComponent(Component.literal("-").append(Component.literal(p.name()).plainCopy().withStyle(ChatFormatting.UNDERLINE)), 0x558B7D6B, false, false).maxScale(1f),
                    Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(6)),
                    Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.05f)));
        }

    }
}
