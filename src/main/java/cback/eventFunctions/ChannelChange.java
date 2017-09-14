package cback.eventFunctions;

import cback.MovieBot;
import cback.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;
import java.util.List;

public class ChannelChange {
    private MovieBot bot;

    public ChannelChange(MovieBot bot) {
        this.bot = bot;
    }

    @EventSubscriber //Set all
    public void setMuteRoleMASS(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String text = message.getContent();
        if (text.equalsIgnoreCase("!setmuteperm") && message.getAuthor().getStringID().equals("73416411443113984")) {
            IGuild guild = MovieBot.getHomeGuild();
            List<IChannel> channelList = guild.getChannels();
            IRole muted = guild.getRoleByID(239233306325942272l);
            for (IChannel channels : channelList) {
                RequestBuffer.request(() -> {
                    try {
                        channels.overrideRolePermissions(muted, EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.EMBED_LINKS, Permissions.ATTACH_FILES));
                    } catch (Exception e) {
                        Util.reportHome(e);
                    }
                });
            }
            System.out.println("Set muted role");
            Util.deleteMessage(message);
        }
    }

    @EventSubscriber //New Channel
    public void newChannel(ChannelCreateEvent event) {
        if (event.getGuild().getStringID().equals(MovieBot.getHomeGuild().getStringID())) {
            //Set muted role
            IGuild guild = event.getClient().getGuildByID(192441520178200577l);
            IRole muted = guild.getRoleByID(239233306325942272l);

            try {
                event.getChannel().overrideRolePermissions(muted, EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.SEND_MESSAGES));
            } catch (Exception e) {
                Util.reportHome(e);
            }
        }
    }
}


