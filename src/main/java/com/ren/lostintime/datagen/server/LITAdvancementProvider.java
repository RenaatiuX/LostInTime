package com.ren.lostintime.datagen.server;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.MobEffectInit;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class LITAdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        registerEffectAdvancements(registries, saver, existingFileHelper);
    }

    private void registerEffectAdvancements(HolderLookup.Provider registries, Consumer<Advancement> writer, ExistingFileHelper existingFileHelper) {
        Advancement tisButAScratch = Advancement.Builder.advancement()
                .display(
                        Items.RED_DYE,
                        Component.translatable("advancement.lostintime.tis_but_a_scratch.title"),
                        Component.translatable("advancement.lostintime.tis_but_a_scratch.desc"), null,
                        FrameType.TASK, true, true, false)
                .addCriterion("has_bleeding", EffectsChangedTrigger.TriggerInstance.hasEffects(
                        MobEffectsPredicate.effects().and(MobEffectInit.BLEEDING.get())))
                .save(writer, ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "tis_but_a_scratch"), existingFileHelper);
    }
}