package com.ren.lostintime.common.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public record FossilFormationConfig(BlockState fossilBlock, BlockState deepslateFossilBlock, RuleTest replaceable,
        RuleTest deepslateReplaceable) implements FeatureConfiguration {

    public static final Codec<FossilFormationConfig> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BlockState.CODEC.fieldOf("fossilBlock").forGetter(FossilFormationConfig::fossilBlock),
                    BlockState.CODEC.fieldOf("deepslateFossilBlock").forGetter(FossilFormationConfig::deepslateFossilBlock),
                    RuleTest.CODEC.fieldOf("replaceable").forGetter(FossilFormationConfig::replaceable),
                    RuleTest.CODEC.fieldOf("deepslateReplaceable").forGetter(FossilFormationConfig::deepslateReplaceable)
            ).apply(instance, FossilFormationConfig::new));
}

