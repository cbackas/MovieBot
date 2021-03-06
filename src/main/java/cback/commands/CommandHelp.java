package cback.commands;

import cback.MovieBot;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHelp implements Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("commands");
    }

    @Override
    public String getSyntax() {
        return "help <command>";
    }

    @Override
    public String getDescription() {
        return "Returns a list of commands (you're looking at it right now)";
    }

    @Override
    public List<Long> getPermissions() {
        return null;
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, MovieBot bot) {
        if (args.length == 1) {
            boolean tripped = false;
            for (Command c : MovieBot.registeredCommands) {
                if (c.getName().equalsIgnoreCase(args[0]) || (c.getAliases() != null && c.getAliases().contains(args[0].toLowerCase()))) {
                    Util.syntaxError(c, message);
                    tripped = true;
                    break;
                }
            }

            if (!tripped) {
                Util.simpleEmbed(message.getChannel(), "Sorry, I couldn't find a command named " + args[0]);
            }
        } else {
            EmbedBuilder embed = Util.getEmbed();
            embed.withTitle("Commands:");

            List<Long> roles = message.getAuthor().getRolesForGuild(guild).stream().map(role -> role.getLongID()).collect(Collectors.toList());
            for (Command c : MovieBot.registeredCommands) {

                if (c.getDescription() != null) {

                    String aliases = "";
                    if (c.getAliases() != null) {
                        aliases = "\n*Aliases:* " + c.getAliases().toString();
                    }

                    if (c.getPermissions() == null) {
                        embed.appendField(c.getSyntax(), c.getDescription() + aliases, false);
                    } else if (!Collections.disjoint(roles, c.getPermissions())) {
                        embed.appendField(MovieBot.getPrefix() + c.getSyntax(), c.getDescription() + aliases, false);
                    }

                }

            }

            embed.withFooterText("You only see commands you have permission to use");

            Util.sendEmbed(message.getAuthor().getOrCreatePMChannel(), embed.withColor(Util.getBotColor()).build());
        }

        Util.deleteMessage(message);

    }

}