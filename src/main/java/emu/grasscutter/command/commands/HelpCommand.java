package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.command.CommandMap;
import emu.grasscutter.game.GenshinPlayer;

import java.util.*;

@Command(label = "help", usage = "help [command]",
        description = "Sends the help message or shows information about a specified command")
public final class HelpCommand implements CommandHandler {

    @Override
    public void execute(GenshinPlayer player, List<String> args) {
        if (args.size() < 1) {
            HashMap<String, CommandHandler> handlers = CommandMap.getInstance().getHandlers();
            List<Command> annotations = new ArrayList<>();
            for (String key : handlers.keySet()) {
                Command annotation = handlers.get(key).getClass().getAnnotation(Command.class);

                if (!Arrays.asList(annotation.aliases()).contains(key)) {
                    if (player != null && !Objects.equals(annotation.permission(), "") && !player.getAccount().hasPermission(annotation.permission()))
                        continue;
                    annotations.add(annotation);
                }
            }

            SendAllHelpMessage(player, annotations);
        } else {
            String command = args.get(0);
            CommandHandler handler = CommandMap.getInstance().getHandler(command);
            StringBuilder builder = new StringBuilder(player == null ? "\nHelp - " : "Help - ").append(command).append(": \n");
            if (handler == null) {
                builder.append("No command found.");
            } else {
                Command annotation = handler.getClass().getAnnotation(Command.class);

                builder.append("   ").append(annotation.description()).append("\n");
                builder.append("   Usage: ").append(annotation.usage());
                if (annotation.aliases().length >= 1) {
                    builder.append("\n").append("   Aliases: ");
                    for (String alias : annotation.aliases()) {
                        builder.append(alias).append(" ");
                    }
                }
                if (player != null && !Objects.equals(annotation.permission(), "") && !player.getAccount().hasPermission(annotation.permission())) {
                    builder.append("\n Warning: You do not have permission to run this command.");
                }
            }

            CommandHandler.sendMessage(player, builder.toString());
        }
    }

    void SendAllHelpMessage(GenshinPlayer player, List<Command> annotations) {
        if (player == null) {
            StringBuilder builder = new StringBuilder("\nAvailable commands:\n");
            annotations.forEach(annotation -> {
                builder.append(annotation.label()).append("\n");
                builder.append("   ").append(annotation.description()).append("\n");
                builder.append("   Usage: ").append(annotation.usage());
                if (annotation.aliases().length >= 1) {
                    builder.append("\n").append("   Aliases: ");
                    for (String alias : annotation.aliases()) {
                        builder.append(alias).append(" ");
                    }
                }

                builder.append("\n");
            });

            CommandHandler.sendMessage(null, builder.toString());
        } else {
            CommandHandler.sendMessage(player, "Available commands:");
            annotations.forEach(annotation -> {
                StringBuilder builder = new StringBuilder(annotation.label()).append("\n");
                builder.append("   ").append(annotation.description()).append("\n");
                builder.append("   Usage: ").append(annotation.usage());
                if (annotation.aliases().length >= 1) {
                    builder.append("\n").append("   Aliases: ");
                    for (String alias : annotation.aliases()) {
                        builder.append(alias).append(" ");
                    }
                }

                CommandHandler.sendMessage(player, builder.toString());
            });
        }
    }
}
