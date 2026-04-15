package com.ren.lostintime.client.screen.book;

import com.ren.lostintime.client.screen.book.components.ImageComponent;
import com.ren.lostintime.client.screen.book.components.TextPageComponent;
import com.ren.lostintime.client.screen.book.components.TitlePageComponent;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IntroductionPage extends Page {

    public IntroductionPage() {
        this.addComponent(
                new TitlePageComponent(Component.literal("Introduction")).maxScale(1.5F)
                        .addUnderline(2, 0x558B7D6B, Inset.symmetric(LayoutValue.px(3), LayoutValue.px(7))),
                Inset.symmetric(LayoutValue.ZERO, LayoutValue.px(1)),
                Dimension.of(DimensionValue.fill(), DimensionValue.none())
        );

        //Just for test
        String introText = "Time was a thief, until now. These pages hold the secrets to defy extinction and reclaim a world that history once forgot.";
        this.addComponent(
                new TextPageComponent(Component.literal(introText), 0x403020, true),
                Inset.of(LayoutValue.px(4), LayoutValue.ZERO, LayoutValue.px(10), LayoutValue.px(10)),
                new Dimension(DimensionValue.fill(), DimensionValue.none())
        );

        this.addComponent(
                new TextPageComponent(Component.literal("- The Chronicler"), 0x604030, false),
                Inset.of(LayoutValue.px(15), LayoutValue.ZERO, LayoutValue.px(30), LayoutValue.ZERO),
                new Dimension(DimensionValue.fill(), DimensionValue.none())
        );

        this.addComponent(
                new ImageComponent(ResourceLocation.withDefaultNamespace("textures/item/compass_16.png"), 16, 16, true),
                Inset.of(LayoutValue.px(20), LayoutValue.ZERO, LayoutValue.ZERO, LayoutValue.ZERO),
                new Dimension(DimensionValue.fill(), DimensionValue.px(52))
        );
    }
}
