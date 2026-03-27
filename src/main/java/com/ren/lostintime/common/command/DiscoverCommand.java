package com.ren.lostintime.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ren.lostintime.common.init.CapabilityInit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.ForgeRegistries;

public class DiscoverCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("discover")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("all")
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    player.getCapability(CapabilityInit.PLAYER_DISCOVERED_PREHISTORIC).ifPresent(cap -> {
                                        ForgeRegistries.ENTITY_TYPES.getValues()
                                                .forEach(type -> {
                                                    var tempEntity = type.create(player.level());
                                                    if (tempEntity != null) {
                                                        cap.discoverEntity(tempEntity);
                                                    }
                                                });
                                    });
                                    context.getSource().sendSuccess(() -> Component.literal("Discovered all prehistoric entities for " + player.getScoreboardName()), true);
                                    return 1;
                                })
                        )
                );
    }

}
