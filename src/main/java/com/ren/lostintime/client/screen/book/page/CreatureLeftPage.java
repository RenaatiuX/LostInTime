package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.components.LinePageComponent;
import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.entity.util.LostInTimeBookDescription;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class CreatureLeftPage extends Page{


    private final LostInTimeBookDescription description;

    public CreatureLeftPage(LostInTimeBookDescription description) {
        this.description = description;
        this.addComponent(new TitlePageComponent(description.getDisplayName(), 0x76644330, false, true).maxScale(1.5f),
                Inset.symmetric(LayoutValue.px(5), LayoutValue.px(3)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.2f)));
        this.addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, 0x76644330),
                Inset.symmetric(LayoutValue.px(3), LayoutValue.px(10)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(2)));
    }
}
