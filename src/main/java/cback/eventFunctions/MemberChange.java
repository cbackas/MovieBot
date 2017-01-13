package cback.eventFunctions;

import cback.MovieBot;
import cback.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserBanEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class MemberChange {
    private MovieBot bot;

    public MemberChange(MovieBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void memberJoin(UserJoinEvent event) {
        IUser user = event.getUser();

        //Mute Check
        /*if (bot.getConfigManager().getConfigArray("muted").contains(event.getUser().getID())) {
            try {
                event.getUser().addRole(event.getGuild().getRoleByID("231269949635559424"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        //Join Counter
        int joined = Integer.parseInt(bot.getConfigManager().getConfigValue("joined"));
        bot.getConfigManager().setConfigValue("joined", String.valueOf(joined + 1));


        String welcomeMessage = "Welcome to " + event.getGuild().getName() + "! We primarily discuss movies but we also discuss other things on occasion. We are an English server.\n" +
                "\n" +
                "**Rule 1:** Stay Civil\n" +
                "\n" +
                "**Rule 2:** No Spam\n" +
                "\n" +
                "**Rule 3:** No Self-Promotion\n" +
                "\n" +
                "**Rule 4:** Keep spoilers in their respective channels.\n" +
                "\n" +
                "**Rule 5:** No NSFW of any kind.\n" +
                "\n" +
                "**Rule 6:** Do not abuse or add bots.\n" +
                "\n" +
                "**Other:** Our rules are subject to change, more in depth rules are on the server, #announcements.\n" +
                "\n" +
                "``Important info``\n" +
                "We are constantly adding new channels and deleting inactive ones, If you don't see your favorite show head over to #suggestions and simply do !suggest 'your show'. It will probably be added soon!";
        try {
            Util.sendPrivateMessage(user, welcomeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventSubscriber
    public void memberLeave(UserLeaveEvent event) {
        IUser user = event.getUser();

        //Mute Check
        if (bot.getConfigManager().getConfigArray("muted").contains(event.getUser().getID())) {
            Util.sendMessage(event.getGuild().getChannelByID("261746338075639810"), user + " is muted and left the server. Their mute will be applied again when/if they return.");
        }

        //Leave Counter
        int left = Integer.parseInt(bot.getConfigManager().getConfigValue("left"));
        bot.getConfigManager().setConfigValue("left", String.valueOf(left + 1));


    }

    @EventSubscriber
    public void memberBanned(UserBanEvent event) {
        IUser user = event.getUser();

        //Mute Check
        if (bot.getConfigManager().getConfigArray("muted").contains(event.getUser().getID())) {
            List<String> mutedUsers = bot.getConfigManager().getConfigArray("muted");
            mutedUsers.remove(user.getID());
            bot.getConfigManager().setConfigValue("muted", mutedUsers);
        }

        //Leave Counter
        int left = Integer.parseInt(bot.getConfigManager().getConfigValue("left"));
        bot.getConfigManager().setConfigValue("left", String.valueOf(left + 1));
    }
}
