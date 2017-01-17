package cback.commands;

import cback.MovieBot;
import cback.MovieRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.List;

public class CommandCommandAdd implements Command {
    @Override
    public String getName() {
        return "addcommand";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "!addcommand [commandName] [Response text]";
    }

    @Override
    public String getDescription() {
        return "Adds a custom command that returns desired response text when you call it";
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList(MovieRoles.ADMIN.id);
    }

    @Override
    public void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getAuthor().getRolesForGuild(guild).contains(guild.getRoleByID(MovieRoles.ADMIN.id))) {

            String text = message.getContent();

            String commandName = args[0];
            String commandResponse = text.split(" ", 3)[2];

            if (commandName != null && commandResponse != null && bot.getCommandManager().getCommandValue(commandName) == null && !MovieBot.getInstance().registeredCommands.contains(commandName)) {
                bot.getCommandManager().setConfigValue(commandName, commandResponse);

                Util.sendMessage(message.getChannel(), "Custom command added: ``" + commandName + "``");
            } else {
                Util.sendMessage(message.getChannel(), "**Usage**: ``!addcommand commandname \"custom response\"``");
            }

            Util.botLog(message);
            Util.deleteMessage(message);
        }
    }
}
