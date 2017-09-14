package cback.eventFunctions;

import cback.MovieBot;
import cback.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class MemberChange {
    private MovieBot bot;

    public MemberChange(MovieBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void memberJoin(UserJoinEvent event) {
        if (event.getGuild().getStringID().equals(MovieBot.getHomeGuild().getStringID())) {
            IUser user = event.getUser();
            IGuild guild = MovieBot.getHomeGuild();

            /**
             * Mute check
             */
            if (bot.getConfigManager().getConfigArray("muted").contains(user.getStringID())) {
                try {
                    user.addRole(guild.getRoleByID(MovieBot.MUTED_ROLE_ID));
                } catch (Exception e) {
                    Util.reportHome(e);
                }
            }

            /**
             * Member counter
             */
            int joinedUsers = Integer.parseInt(bot.getConfigManager().getConfigValue("joined"));
            bot.getConfigManager().setConfigValue("joined", String.valueOf(joinedUsers + 1));
        }
    }

    @EventSubscriber
    public void memberLeave(UserLeaveEvent event) {
        if (event.getGuild().getStringID().equals(MovieBot.getHomeGuild().getStringID())) {
            IUser user = event.getUser();
            IGuild guild = MovieBot.getHomeGuild();

            /**
             * Mute check
             */
            if (bot.getConfigManager().getConfigArray("muted").contains(event.getUser().getStringID())) {
                Util.sendMessage(guild.getChannelByID(MovieBot.STAFF_CH_ID), user + " is muted and left the server. Their mute will be applied again when/if they return.");
            }

            /**
             * Member counter
             */
            int left = Integer.parseInt(bot.getConfigManager().getConfigValue("left"));
            bot.getConfigManager().setConfigValue("left", String.valueOf(left + 1));
        }
    }

    @EventSubscriber
    public void memberBanned(UserBanEvent event) {
        if (event.getGuild().getStringID().equals(MovieBot.getHomeGuild().getStringID())) {
            IUser user = event.getUser();
            IGuild guild = MovieBot.getHomeGuild();

            /**
             * Mute check
             */
            if (bot.getConfigManager().getConfigArray("muted").contains(user.getStringID())) {
                List<String> mutedUsers = bot.getConfigManager().getConfigArray("muted");
                mutedUsers.remove(user.getStringID());
                bot.getConfigManager().setConfigValue("muted", mutedUsers);
            }

            /**
             * Member counter
             */
            int left = Integer.parseInt(bot.getConfigManager().getConfigValue("left"));
            bot.getConfigManager().setConfigValue("left", String.valueOf(left + 1));
        }
    }
}