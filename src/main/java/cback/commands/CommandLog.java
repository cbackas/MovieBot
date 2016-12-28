package cback.commands;

import cback.MovieBot;
import cback.MovieRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;

import java.util.Arrays;
import java.util.List;

public class CommandLog implements Command {
    @Override
    public String getName() {
        return "addlog";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("log");
    }

    @Override
    public void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getAuthor().getRolesForGuild(guild).contains(guild.getRoleByID(MovieRoles.STAFF.id))) {

            if (args.length >= 1) {
                List<IRole> userRoles = message.getAuthor().getRolesForGuild(guild);
                if (Util.permissionCheck(message, MovieRoles.ADMIN.id) || Util.permissionCheck(message, MovieRoles.MOD.id)) {
                    Util.botLog(message);

                    String finalText = message.getFormattedContent().split(" ", 2)[1];
                    Util.sendMessage(guild.getChannelByID(MovieBot.LOG_CHANNEL_ID), "```" + finalText + "\n- " + message.getAuthor().getDisplayName(guild) + "```");
                    Util.sendMessage(message.getChannel(), "Log added. " + guild.getChannelByID(MovieBot.LOG_CHANNEL_ID).mention());
                    Util.deleteMessage(message);
                } else {
                    Util.sendMessage(message.getChannel(), "You don't have permission to add logs.");
                }
            } else {
                Util.sendMessage(message.getChannel(), "Usage: !addlog <text>");
            }
        }
    }

}
