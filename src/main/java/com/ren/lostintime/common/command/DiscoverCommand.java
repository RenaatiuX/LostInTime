package com.ren.lostintime.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ren.lostintime.common.init.CapabilityInit;
import com.ren.lostintime.common.item.PrehistoricBookItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class DiscoverCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("discover")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.literal("all")
                                .executes(context -> {
                                    var players = EntityArgument.getPlayers(context, "player");
                                    for (var player : players) {
                                        var heldItem = player.getMainHandItem();
                                        if (heldItem.isEmpty()) {
                                            heldItem = player.getOffhandItem();
                                        }
                                        if (heldItem.isEmpty()) {
                                            context.getSource().sendSystemMessage(Component.literal("Player ").append(Objects.requireNonNull(player.getCustomName())).append(Component.literal("has no book item to discover anything in neither main hand nor off hand")));
                                            return 1;
                                        }
                                        ItemStack finalHeldItem = heldItem;
                                        ForgeRegistries.ENTITY_TYPES.getValues()
                                                .forEach(type -> {
                                                    var tempEntity = type.create(player.level());
                                                    if (tempEntity != null) {
                                                        tempEntity.getCapability(CapabilityInit.ENTITY_DESCRIPTION_CAPABILITY).ifPresent(cap -> {
                                                                    PrehistoricBookItem.discoverTimePeriod(finalHeldItem, cap.getPeriod());
                                                                    PrehistoricBookItem.discoverEntity(finalHeldItem, type);
                                                                }
                                                        );
                                                    }
                                                });
                                        context.getSource().sendSuccess(() -> Component.literal("Discovered all prehistoric entities for " + player.getScoreboardName()), true);
                                    }

                                    return 1;
                                })
                        )
                );
    }

}
