package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.components.LinePageComponent;
import com.ren.lostintime.client.screen.book.components.MapPageComponent;
import com.ren.lostintime.client.screen.book.components.TimePeriodSliderPageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.common.entity.util.LostInTimeBookDescription;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class CreatureRightPage extends Page{

    public CreatureRightPage(LostInTimeBookDescription description) {

        disableScisscor();

        this.addComponent(new TimePeriodSliderPageComponent(description.getPeriod()),
                Inset.symmetric(LayoutValue.percentOfParent(0.02f), LayoutValue.px(2)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.06f)));

        this.addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, 0x76644330),
                Inset.symmetric(LayoutValue.px(2), LayoutValue.px(7)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(1)));

        this.addComponent(new MapPageComponent(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "coordinates/anomalocaris.csv")),
                Inset.of(LayoutValue.px(4), LayoutValue.px(0), LayoutValue.px(4), LayoutValue.px(4)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.6f)));
    }
}
