package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.components.TimePeriodSliderPageComponent;
import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.entity.util.TimePeriod;
import net.minecraft.network.chat.Component;

public class TestPage extends Page{

    public TestPage() {
        disableScisscor();
        this.addComponent(new TitlePageComponent(Component.literal("Test Page")).maxScale(1.5f)
                        .addUnderline(2, 0x558B7D6B, Inset.symmetric(LayoutValue.px(3),
                                LayoutValue.px(7))), Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );
        for (TimePeriod p : TimePeriod.values()) {
            this.addComponent(new TimePeriodSliderPageComponent(p), Inset.of(LayoutValue.px(1), LayoutValue.px(1), LayoutValue.px(4), LayoutValue.px(1)),
                    Dimension.of(DimensionValue.fill(), DimensionValue.px(10))
            );
        }
    }
}
