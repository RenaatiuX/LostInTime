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
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.offset(dx, 0, dz));
            }
        }

        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
        BlockPos back = blockPos.relative(dir.getOpposite());

        tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, back);
        tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, back.relative(dir.getOpposite()));

        if (blockPos.getY() > pMaxFreeTreeHeight * 0.7) {
            tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.above());
            tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.above().offset(1, 0, 0));
            tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.above().offset(-1, 0, 0));
            tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.above().offset(0, 0, 1));
            tryPlaceLeaf(pLevel, pBlockSetter, pRandom, pConfig, blockPos.above().offset(0, 0, -1));
        }
    }

    @Override
    public int foliageHeight(RandomSource pRandom, int pHeight, TreeConfiguration pConfig) {
        return 1;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource pRandom, int pLocalX, int pLocalY, int pLocalZ, int pRange, boolean pLarge) {
        return false;
    }
}
