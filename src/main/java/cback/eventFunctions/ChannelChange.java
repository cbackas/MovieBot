package cback.eventFunctions;

import cback.MovieBot;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
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
            IDiscordClient client = event.getClient();
            if (text.equalsIgnoreCase("!setmuteperm") && message.getAuthor().getID().equals("73416411443113984")) {
                List<IChannel> channelList = client.getGuildByID("256248900124540929").getChannels();
                IGuild guild = event.getClient().getGuildByID("256248900124540929");
                IRole muted = guild.getRoleByID("239233306325942272");
                for (IChannel channels : channelList) {
                    RequestBuffer.request(() -> {
                        try {
                            channels.overrideRolePermissions(muted, EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.EMBED_LINKS, Permissions.ATTACH_FILES));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                System.out.println("Set muted role");
                Util.deleteMessage(message);
            }
    }

    @EventSubscriber //New Channel
    public void newChannel(ChannelCreateEvent event) {
        //Set muted role
        IGuild guild = event.getClient().getGuildByID("256248900124540929");
        IRole muted = guild.getRoleByID("231269949635559424");
        IRole embedMuted = guild.getRoleByID("239233306325942272");
        try {
            event.getChannel().overrideRolePermissions(muted, EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.SEND_MESSAGES));
            event.getChannel().overrideRolePermissions(embedMuted, EnumSet.noneOf(Permissions.class), EnumSet.of(Permissions.EMBED_LINKS, Permissions.ATTACH_FILES));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}


