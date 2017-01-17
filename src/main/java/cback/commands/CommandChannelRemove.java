package cback.commands;

import cback.MovieBot;
import cback.MovieRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.List;

public class CommandChannelRemove implements Command {
    @Override
    public String getName() {
        return "deletechannel";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("removechannel");
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getAuthor().getRolesForGuild(guild).contains(guild.getRoleByID(MovieRoles.ADMIN.id))) {
            List<IChannel> mentionsC = message.getChannelMentions();
            if (!mentionsC.isEmpty()) {
                for (IChannel c : mentionsC) {
                    try {

                        Util.sendLog(message, "Deleted " + c.getName() + " channel.");

                        c.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (args[0].equalsIgnoreCase("here")) {
                try {

                    IChannel here = message.getChannel();

                    Util.sendLog(message, "Deleted " + here.getName() + " channel.");
                    here.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Util.sendMessage(message.getChannel(), "**ERROR**: Couldn't find channel to delete.");
            }

            Util.botLog(message);
            Util.deleteMessage(message);
        }
    }

}
