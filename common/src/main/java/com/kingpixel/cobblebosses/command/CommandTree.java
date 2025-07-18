package com.kingpixel.cobblebosses.command;

import com.kingpixel.cobblebosses.CobbleBosses;
import com.kingpixel.cobblebosses.model.AutoSpawner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandTree {

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("cobblebosses")
                .then(literal("reload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(CommandTree::reload)
                )
        );
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        CobbleBosses.load();
        AutoSpawner.restart();
        context.getSource().sendFeedback(() -> Text.literal("§aCobbleBosses reloaded! AutoSpawner restarted automatically."), false);
        return 1;
    }
}
