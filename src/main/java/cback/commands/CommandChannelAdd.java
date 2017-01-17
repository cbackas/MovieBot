package cback.commands;

import cback.MovieBot;
import cback.MovieRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandChannelAdd implements Command {
    @Override
    public String getName() {
        return "addchannel";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("newchannel", "createchannel");
    }

    @Override
    public String getSyntax() {
        return "!addchannel [channelName] | [channelName]";
    }

    @Override
    public String getDescription() {
        return "Creates a new channel with the desired name and logs it";
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList(MovieRoles.ADMIN.id);
    }

    @Override
    public void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getAuthor().getRolesForGuild(guild).contains(guild.getRoleByID(MovieRoles.ADMIN.id))) {

            String channelName = Arrays.stream(args).collect(Collectors.joining("-"));
            String channelNames[] = channelName.split("-\\|-");

            for (String c : channelNames) {
                RequestBuffer.request(() -> {
                    try {
                        guild.createChannel(c);

                        Util.sendLog(message, "Added " + c + " channel.");
                    } catch (DiscordException | MissingPermissionsException e) {
                        e.printStackTrace();

                        Util.sendMessage(message.getChannel(), "**" + c + "** channel creation failed.");
                    }
                });
            }

            Util.botLog(message);
            Util.deleteMessage(message);
        }
    }

}
