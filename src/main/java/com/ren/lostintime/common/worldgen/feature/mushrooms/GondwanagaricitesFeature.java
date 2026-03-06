package com.ren.lostintime.common.worldgen.feature.mushrooms;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class GondwanagaricitesFeature extends AbstractHugeMushroomFeature {

    public GondwanagaricitesFeature(Codec<HugeMushroomFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    protected int getTreeRadiusForHeight(int p_65094_, int p_65095_, int pFoliageRadius, int pY) {
        int i = 0;
        if (pY < p_65095_ && pY >= p_65095_ - 3) {
            i = pFoliageRadius;
        } else if (pY == p_65095_) {
            i = pFoliageRadius;
        }

        return i;
    }

    @Override
    protected int getTreeHeight(RandomSource pRandom) {
        return pRandom.nextInt(2) + 4;
    }

    @Override
    protected void makeCap(LevelAccessor pLevel, RandomSource pRandom, BlockPos pPos, int pTreeHeight, BlockPos.MutableBlockPos pMutablePos, HugeMushroomFeatureConfiguration pConfig) {
        for (int yOffset = -1; yOffset <= 0; ++yOffset) {
            for (int x = -2; x <= 2; ++x) {
                for (int z = -2; z <= 2; ++z) {

                    if (!hasBlockAt(x, z, yOffset)) {
                        continue;
                    }

                    pMutablePos.setWithOffset(pPos, x, pTreeHeight + yOffset, z);

                    if (pLevel.getBlockState(pMutablePos).canBeReplaced()) {
                        boolean isUp = !hasBlockAt(x, z, yOffset + 1);
                        boolean isWest = (x <= 0) && !hasBlockAt(x - 1, z, yOffset);
                        boolean isEast = (x >= 0) && !hasBlockAt(x + 1, z, yOffset);
                        boolean isNorth = (z <= 0) && !hasBlockAt(x, z - 1, yOffset);
                        boolean isSouth = (z >= 0) && !hasBlockAt(x, z + 1, yOffset);

                        BlockState state = pConfig.capProvider.getState(pRandom, pPos)
                                .setValue(HugeMushroomBlock.UP, isUp)
                                .setValue(HugeMushroomBlock.WEST, isWest)
                                .setValue(HugeMushroomBlock.EAST, isEast)
                                .setValue(HugeMushroomBlock.NORTH, isNorth)
                                .setValue(HugeMushroomBlock.SOUTH, isSouth)
                                .setValue(HugeMushroomBlock.DOWN, false);

                        this.setBlock(pLevel, pMutablePos, state);
                    }
                }
            }
        }
    }

    private boolean hasBlockAt(int nx, int nz, int yOffset) {
        if (nx < -2 || nx > 2 || nz < -2 || nz > 2) return false;
        if (Math.abs(nx) == 2 && Math.abs(nz) == 2) return false;

        if (yOffset == 0) {
            return Math.abs(nx) + Math.abs(nz) <= 2;
        } else if (yOffset == -1) {
            return !(Math.abs(nx) <= 1 && Math.abs(nz) <= 1);
        }
        return false;
    }
}
