package com.ren.lostintime.client.screen.book.page;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;
import com.ren.lostintime.client.screen.book.components.*;
import com.ren.lostintime.client.screen.book.util.Dimension;
import com.ren.lostintime.client.screen.book.util.DimensionValue;
import com.ren.lostintime.client.screen.book.util.Inset;
import com.ren.lostintime.client.screen.book.util.LayoutValue;
import com.ren.lostintime.client.util.ScreenRenderingUtils;
import com.ren.lostintime.common.entity.util.LostInTimeBookDescription;
import com.ren.lostintime.common.init.AttributeInit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class CreatureLeftPage extends Page {


    private final LostInTimeBookDescription description;

    public CreatureLeftPage(LostInTimeBookDescription description) {
        this.description = description;
        ScreenRenderingUtils.addTitleComponents(this, description.getDisplayName(), 0x76644330);
        var entity = description.getEntityType().create(Minecraft.getInstance().level);

        this.addComponent(new ImageComponent(description.icon(), 256, 256, true),
                Inset.symmetric(LayoutValue.px(4), LayoutValue.px(2)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.4f)));
        this.addComponent(new LinePageComponent(LinePageComponent.Orientation.HORIZONTAL, 0x76644330),
                Inset.symmetric(LayoutValue.px(1), LayoutValue.px(7)),
                Dimension.of(DimensionValue.fill(), DimensionValue.px(1)));
        var horizontalLayout = new HorizontalFlowLayout();
        horizontalLayout.addComponent(new StackedImageComponent(9,9,256,256,true)
                .addImage(PrehistoricBookScreen.WIDGETS_LOCATION, 52, 9)
                .addImage(PrehistoricBookScreen.WIDGETS_LOCATION, 52, 0),
                Inset.ZERO,
                Dimension.of(DimensionValue.percent(0.1f), DimensionValue.fill()));
        horizontalLayout.addComponent(new TextPageComponent(Component.translatable("prehistoric.book." + LostInTime.MODID + ".creature.health.description", entity instanceof LivingEntity living ? living.getMaxHealth() : 0.0d), 0x76644330, false),
                Inset.ZERO,
                Dimension.of(DimensionValue.fill(), DimensionValue.fill()));
        this.addComponent(horizontalLayout,
                Inset.symmetric(LayoutValue.px(2), LayoutValue.px(2)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.05f)));

        var horizontalLayoutHunger = new HorizontalFlowLayout();
        horizontalLayoutHunger.addComponent(new StackedImageComponent(9,9,256,256, true)
                .addImage(PrehistoricBookScreen.WIDGETS_LOCATION, 16, 36),
                Inset.ZERO,
                Dimension.of(DimensionValue.percent(0.1f), DimensionValue.fill()));
        horizontalLayoutHunger.addComponent(new TextPageComponent(Component.translatable("prehistoric.book." + LostInTime.MODID + ".creature.hunger.description", entity instanceof LivingEntity living ? living.getAttributeValue(AttributeInit.MAX_HUNGER.get()) : 0.0d), 0x76644330, false),
                Inset.ZERO,
                Dimension.of(DimensionValue.fill(), DimensionValue.fill()));

        this.addComponent(horizontalLayoutHunger,
                Inset.symmetric(LayoutValue.px(0), LayoutValue.px(2)),
                Dimension.of(DimensionValue.fill(), DimensionValue.percent(0.05f)));

    }
}
