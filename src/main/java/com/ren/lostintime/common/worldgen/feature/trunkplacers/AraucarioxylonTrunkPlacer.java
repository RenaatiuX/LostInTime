package com.ren.lostintime.common.worldgen.feature.trunkplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ren.lostintime.common.init.TrunkPlacerTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.BiConsumer;

public class AraucarioxylonTrunkPlacer extends TrunkPlacer {

    public static final Codec<AraucarioxylonTrunkPlacer> CODEC = RecordCodecBuilder.create(araucarioxylonTrunkPlacerInstance ->
            trunkPlacerParts(araucarioxylonTrunkPlacerInstance).apply(araucarioxylonTrunkPlacerInstance, AraucarioxylonTrunkPlacer::new));

    public AraucarioxylonTrunkPlacer(int pBaseHeight, int pHeightRandA, int pHeightRandB) {
        super(pBaseHeight, pHeightRandA, pHeightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerTypeInit.ARAUCARIOXYLON_TRUNk_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> result = Lists.newArrayList();

        BlockPos blockpos = pPos.below();
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos, pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.east(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south(), pConfig);
        setDirtAt(pLevel, pBlockSetter, pRandom, blockpos.south().east(), pConfig);

        int height = this.getTreeHeight(pRandom);

        for (int y = 0; y < height; ++y) {
            place(pLevel, pBlockSetter, pRandom, pPos.offset(0, y, 0), pConfig);
            place(pLevel, pBlockSetter, pRandom, pPos.offset(1, y, 0), pConfig);
            place(pLevel, pBlockSetter, pRandom, pPos.offset(0, y, 1), pConfig);
            place(pLevel, pBlockSetter, pRandom, pPos.offset(1, y, 1), pConfig);
        }

        int startBranchY = (int) (height * 0.3);

        for (int y = startBranchY; y < height - 2; y += 2 + pRandom.nextInt(2)) {
            float heightPercent = (float) (y - startBranchY) / (height - startBranchY);
            int baseLength = 2 + pRandom.nextInt(2);
            int branchLength = Math.max(1, Math.min(3, (int) (baseLength * (1.2f - heightPercent))));

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos branchStart = switch (dir) {
                    case NORTH -> pPos.offset(pRandom.nextInt(2), y, 0).relative(dir);
                    case SOUTH -> pPos.offset(pRandom.nextInt(2), y, 1).relative(dir);
                    case WEST -> pPos.offset(0, y, pRandom.nextInt(2)).relative(dir);
                    case EAST -> pPos.offset(1, y, pRandom.nextInt(2)).relative(dir);
                    default -> pPos.offset(0, y, 0);
                };
                BlockPos currentBranchPos = branchStart;

                for (int b = 0; b < branchLength; b++) {
                    place(pLevel, pBlockSetter, pRandom, currentBranchPos, pConfig);
                    result.add(new FoliagePlacer.FoliageAttachment(currentBranchPos.above(), 0, false));
                    currentBranchPos = currentBranchPos.relative(dir);
                    if (b > 1 && pRandom.nextFloat() < 0.3f) {
                        currentBranchPos = currentBranchPos.below();
                    }
                }
                result.add(new FoliagePlacer.FoliageAttachment(currentBranchPos.above(), 0, false));
            }
        }
        result.add(new FoliagePlacer.FoliageAttachment(pPos.offset(0, height, 0), 0, false));
        result.add(new FoliagePlacer.FoliageAttachment(pPos.offset(1, height, 0), 0, false));
        result.add(new FoliagePlacer.FoliageAttachment(pPos.offset(0, height, 1), 0, false));
        result.add(new FoliagePlacer.FoliageAttachment(pPos.offset(1, height, 1), 0, false));

        return result;
    }

    private void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, TreeConfiguration config) {
        if (TreeFeature.validTreePos(level, pos)) {
            blockSetter.accept(pos, config.trunkProvider.getState(random, pos));
        }
    }
}
