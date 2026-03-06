package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITY_TYPES, LostInTime.MODID);

    public static final RegistryObject<BlockEntityType<IdentificationBE>> IDENTIFICATION_TABLE = BLOCK_ENTITY_TYPES.register(
            "identification_table", () -> BlockEntityType.Builder.of(IdentificationBE::new,
                    BlockInit.IDENTIFICATION_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<SoulExtractorBE>> SOUL_EXTRACTOR = BLOCK_ENTITY_TYPES.register(
            "soul_extractor", () -> BlockEntityType.Builder.of(SoulExtractorBE::new,
                    BlockInit.SOUL_EXTRACTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<SoulConfiguratorBE>> SOUL_CONFIGURATOR = BLOCK_ENTITY_TYPES.register("soul_configurator", () ->
            BlockEntityType.Builder.of(SoulConfiguratorBE::new, BlockInit.SOUL_CONFIGURATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<TransfiguratorBE>> TRANSFIGURATOR = BLOCK_ENTITY_TYPES.register("transfigurator", () ->
            BlockEntityType.Builder.of(TransfiguratorBE::new, BlockInit.TRANSFIGURATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<LITSignBE>> LIT_SIGN =
            BLOCK_ENTITY_TYPES.register( "lit_sign", () ->
                    BlockEntityType.Builder.of(LITSignBE::new,
                                    BlockInit.ARAUCARIOXYLON_SIGN.get(),
                                    BlockInit.ARAUCARIOXYLON_WALL_SIGN.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<LITHangingSignBE>> LIT_HANGING_SIGN =
            BLOCK_ENTITY_TYPES.register( "lit_hanging_sign", () ->
                    BlockEntityType.Builder.of(LITHangingSignBE::new,
                                    BlockInit.ARAUCARIOXYLON_HANGING_SIGN.get(),
                                    BlockInit.ARAUCARIOXYLON_WALL_HANGING_SIGN.get())
                            .build(null));

}
