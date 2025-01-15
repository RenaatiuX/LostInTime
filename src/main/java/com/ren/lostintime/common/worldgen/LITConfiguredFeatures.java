package com.ren.lostintime.common.worldgen;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.block.MangoFruitBlock;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class LITConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGO_TREE_KEY = registerKey("mango");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, MANGO_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlockInit.MANGO_LOG.get()),
                //5 Base height, 4 Height variation and 3 Number of extra tall blocks that can appear as roots or branches at the base.
                new StraightTrunkPlacer(5, 4, 3),
                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                        .add(BlockInit.MANGO_LEAVES.get().defaultBlockState(), 90)
                        .add(BlockInit.MANGO_FRUIT_LEAVES.get().defaultBlockState()
                                .setValue(MangoFruitBlock.AGE, 3), 20).build()),
                //2 Radius of the base foliage, 0 Variation in foliage height and 3 Maximum height of foliage.
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                //1 Height of the bottom layer ,0 Variation in the height of the middle layer and2 Maximum height of the top layer.
                new TwoLayersFeatureSize(1, 0, 2)).build());
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(LostInTime.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }


}
