package cback;

import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.StatusType;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    /**
     * Check for airings (and delete messages from old airings) every X seconds
     */
    private static final int CHECK_AIRING_INTERVAL = 300; //5 minutes
    /**
     * Number of seconds in one day
     */
    private static final int DAILY_INTERVAL = 86400; //24 hours
    /**
     * Send alert if show airs within X seconds from time of checking
     */
    private static final int ALERT_TIME_THRESHOLD = 660; //11 minutes
    /**
     * Delete message from announcements channel if show aired over X seconds from time of checking
     */
    private static final int DELETE_THRESHOLD = 7000; //~2 hours

    private MovieBot bot;

    public Scheduler(MovieBot bot) {
        this.bot = bot;
        onInit();
    }

    private void onInit() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

        int time = Util.getCurrentTime(); //current epoch time in seconds

        //update user count at midnight every night
        int currentTimeEST = time - getOffset(); //EST time, second offset changes depending on daylight savings
        int midnightWaitTime = roundUp(currentTimeEST, DAILY_INTERVAL) - currentTimeEST + 45; //seconds until midnight
        exec.scheduleAtFixedRate(() -> {

            updateUserCount();
            resetUserChange();
            //Set status
            bot.getClient().changePresence(StatusType.ONLINE, ActivityType.WATCHING,"all of your messages. Type " + MovieBot.prefix + "help");

        }, midnightWaitTime, DAILY_INTERVAL, TimeUnit.SECONDS);
    }

    private static int getOffset() {
        //winter - 18000s
        //summer - 14400s

        boolean inSavingsTime = TimeZone.getTimeZone("US/Eastern").inDaylightTime( new Date() );
        if (inSavingsTime) {
            return 14400;
        } else {
            return 18000;
        }
    }


    /**
     * Update the number of Lounge server members in the config
     */
    public void updateUserCount() {
        IGuild loungeGuild = bot.getHomeGuild();
        if (loungeGuild != null) {
            bot.getConfigManager().setConfigValue("userCount", String.valueOf(loungeGuild.getUsers().size()));
        }
    }

    /**
     * Reset daily user change
     */
    public void resetUserChange() {
        bot.getConfigManager().setConfigValue("left", "0");
        bot.getConfigManager().setConfigValue("joined", "0");
    }

    /**
     * Rounds i to next number divisible by v
     *
     * @param i
     * @param v
     * @return the rounded number divisible by v
     */
    public static int roundUp(double i, int v) {
        Double rounded = Math.ceil(i / v) * v;
        return rounded.intValue();
    }
}