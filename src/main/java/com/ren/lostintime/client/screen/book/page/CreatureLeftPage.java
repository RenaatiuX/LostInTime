package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.client.screen.book.components.ImageComponent;
import com.ren.lostintime.client.screen.book.components.LinePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import com.ren.lostintime.common.entity.util.LostInTimeBookDescription;

public class CreatureLeftPage extends Page {


    private final LostInTimeBookDescription description;

    public CreatureLeftPage(LostInTimeBookDescription description) {
        this.description = description;
        ScreenRenderingUtils.addTitleComponents(this, description.getDisplayName(), 0x76644330);

        this.addComponent(new ImageComponent(description.icon(), 256, 256, true),
                Inset.symmetric(LayoutValue.px(4), LayoutValue.px(2)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.4f)));
        this.addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, 0x76644330),
                Inset.symmetric(LayoutValue.px(1), LayoutValue.px(7)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(1)));
    }
}
