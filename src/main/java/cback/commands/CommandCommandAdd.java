package cback.commands;

import cback.MovieBot;
import cback.MovieRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

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
        return "addcommand commandname \"custom response\"";
    }

    @Override
    public String getDescription() {
        return "Creates a simple custom command";
    }

    @Override
    public List<Long> getPermissions() {
        return Arrays.asList(MovieRoles.ADMIN.id, MovieRoles.NETWORKMOD.id);
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, MovieBot bot) {
        String commandName = args[0];
        String commandResponse = content.split(" ", 3)[2];

        if (commandName != null && commandResponse != null && bot.getCommandManager().getCommandValue(commandName) == null && !MovieBot.getInstance().registeredCommands.contains(commandName)) {
            bot.getCommandManager().setConfigValue(commandName, commandResponse);

            Util.simpleEmbed(message.getChannel(), "Custom command added: ``" + commandName + "``");
        } else {
            Util.syntaxError(this, message);
        }

        Util.deleteMessage(message);
    }
}
