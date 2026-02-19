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

        int branchCount = 10 + pRandom.nextInt(6);

        for (int i = 0; i <= branchCount; ++i) {
            int branchY = (int) (height * (0.25 + (i * 0.05)));
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);

            BlockPos branchStart = pPos.offset(0, branchY, 0).relative(dir);
            int branchLength = 2 + pRandom.nextInt(2);

            for (int b = 0; b < branchLength; b++) {
                place(pLevel, pBlockSetter, pRandom, branchStart, pConfig);
                branchStart = branchStart.relative(dir);
            }

            if (pRandom.nextBoolean()) {
                //nothing
            } else {
                branchStart = branchStart.below();
            }

            place(pLevel, pBlockSetter, pRandom, branchStart, pConfig);

            result.add(new FoliagePlacer.FoliageAttachment(branchStart, 0, false));
        }

        return result;
    }

    private void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, TreeConfiguration config) {
        if (TreeFeature.validTreePos(level, pos)) {
            blockSetter.accept(pos, config.trunkProvider.getState(random, pos));
        }
    }
}
