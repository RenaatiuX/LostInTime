package com.ren.lostintime.common.worldgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ren.lostintime.common.init.FoliagePlacerTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class AraucarioxylonFoliagePlacer extends FoliagePlacer {

    public static final Codec<AraucarioxylonFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
            foliagePlacerParts(instance).apply(instance, AraucarioxylonFoliagePlacer::new));

    public AraucarioxylonFoliagePlacer(IntProvider pRadius, IntProvider pOffset) {
        super(pRadius, pOffset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerTypeInit.ARAUCARIOXYLON_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader pLevel, FoliageSetter pBlockSetter, RandomSource pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment, int pFoliageHeight, int pFoliageRadius, int pOffset) {
        BlockPos blockPos = pAttachment.pos();

        tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (pRandom.nextFloat() < 0.60f) {
                tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.relative(dir));
            }
        }

        BlockPos branchLevel = blockPos.below();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (pRandom.nextFloat() < 0.30f) {
                tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, branchLevel.relative(dir));
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return 2;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return false;
    }
}
