package com.ren.lostintime.common.event;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.command.DiscoverCommand;
import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.entity.util.PlayerDiscoveredPrehistoric;
import com.ren.lostintime.common.entity.util.PlayerDiscoveredPrehistoricImpl;
import com.ren.lostintime.common.init.*;
import com.ren.lostintime.common.villager.LITItemTrade;
import com.ren.lostintime.common.villager.helper.RandomItemStackSource;
import com.ren.lostintime.datagen.server.LITTags;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = LostInTime.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "player_discovery"), new ICapabilitySerializable() {

                final LazyOptional<PlayerDiscoveredPrehistoric> optional = LazyOptional.of(() -> new PlayerDiscoveredPrehistoricImpl(player));

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == CapabilityInit.PLAYER_DISCOVERED_PREHISTORIC)
                        return optional.cast();
                    return LazyOptional.empty();
                }

                @Override
                public Tag serializeNBT() {
                    return optional.map(PlayerDiscoveredPrehistoric::serializeNBT).orElse(new CompoundTag());
                }

                @Override
                public void deserializeNBT(Tag nbt) {
                    if (nbt instanceof CompoundTag tag){
                        optional.ifPresent(playerDiscoveredPrehistoric -> playerDiscoveredPrehistoric.deserializeNBT(tag));
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void commandRegisterEvent(RegisterCommandsEvent event) {
        event.getDispatcher().register(DiscoverCommand.register());
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractIllager illager)) return;
        if (event.getLevel().isClientSide()) return;

        illager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(illager, Dodo.class, true));
    }

    @SubscribeEvent
    public static void AddFossilMasterTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerInit.FOSSIL_MASTER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            trades.get(1).add(((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.BONE, 8),
                    new ItemStack(Items.EMERALD, 1),
                    20, 2, 0.02F)));
            trades.get(1).add(new LITItemTrade(
                    RandomItemStackSource.of(LITTags.Items.UNIDENTIFIED_FOSSIL, 1),
                    RandomItemStackSource.empty(),
                    RandomItemStackSource.of(Items.EMERALD, 1), 12, 2, 0.02F));

            trades.get(2).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(Items.BRUSH, 1),
                    8, 2, 0.02F));

            trades.get(3).add(new LITItemTrade(
                    RandomItemStackSource.of(Items.EMERALD, 8),
                    RandomItemStackSource.empty(),
                    RandomItemStackSource.of(ItemInit.DODO_FOSSIL_MOUNT.get(), 1), 12, 2, 0.02F));
            trades.get(3).add(new LITItemTrade(
                    RandomItemStackSource.of(Items.EMERALD, 2),
                    RandomItemStackSource.empty(),
                    RandomItemStackSource.of(Blocks.BONE_BLOCK.asItem(), 1), 10, 2, 0.02F));

            trades.get(4).add(new LITItemTrade(
                    RandomItemStackSource.of(Items.BOOK, 1),
                    RandomItemStackSource.of(Items.EMERALD, 20),
                    RandomItemStackSource.of(Blocks.BONE_BLOCK.asItem(), 1), 12, 2, 0.02F));
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();
        BlockState state = event.getState();
        ItemStack tool = player.getMainHandItem();

        int levelEnchant = EnchantmentHelper.getTagEnchantmentLevel(EnchantmentInit.FOSSIL_KNOWLEDGE.get(), tool);

        if (levelEnchant <= 0) return;

        if (!state.is(BlockInit.CRETACEOUS_FOSSIL_BLOCK.get())) return;

        float chance = 0.10F * levelEnchant;

        if (level.random.nextFloat() < chance) {
            event.setExpToDrop(0);
            Block.popResource(level, event.getPos(), new ItemStack(ItemInit.CRETACEOUS_FOSSIL.get()));
        }
    }

    @SubscribeEvent
    public static void onAxeStrip(BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction() == ToolActions.AXE_STRIP) {
            if (event.getState().is(BlockInit.ARAUCARIOXYLON_LOG.get())) {
                event.setFinalState(BlockInit.STRIPPED_ARAUCARIOXYLON_LOG.get().defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, event.getState().getValue(RotatedPillarBlock.AXIS)));
            }

            if (event.getState().is(BlockInit.ARAUCARIOXYLON_WOOD.get())) {
                event.setFinalState(BlockInit.STRIPPED_ARAUCARIOXYLON_WOOD.get().defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, event.getState().getValue(RotatedPillarBlock.AXIS)));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(MobEffectInit.INFECTION.get())) {

            event.setCanceled(true);

            if (entity instanceof Player player && !entity.level().isClientSide) {
                if (player.tickCount % 40 == 0 || event.getAmount() > 1.0F) {

                    player.displayClientMessage(
                            Component.translatable("message.lostintime.infection_block")
                                    .withStyle(ChatFormatting.DARK_GREEN),
                            true
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(MobEffectInit.FRACTURE.get())) {
            int amplifier = entity.getEffect(MobEffectInit.FRACTURE.get()).getAmplifier();

            if (amplifier >= 2) {
                entity.hurt(entity.damageSources().fall(), 1.0F); // 1.0F es medio corazón
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(MobEffectInit.FRACTURE.get())) {
            int amplifier = entity.getEffect(MobEffectInit.FRACTURE.get()).getAmplifier();

            float actualdistance = event.getDistance();

            if (actualdistance > 0.5F) {
                float extraDistance = 3.0F + (amplifier * 1.0F);

                event.setDistance(actualdistance + extraDistance);

                event.setDamageMultiplier(event.getDamageMultiplier() + 0.5F);
            }
        }
    }
}
