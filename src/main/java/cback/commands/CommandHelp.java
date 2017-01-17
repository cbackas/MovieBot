package cback.commands;

import cback.MovieBot;
import cback.MovieRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

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
        return "!help";
    }

    @Override
    public String getDescription() {
        return "Returns a list of commands (you're looking at it right now)";
    }

    @Override
    public List<String> getPermissions() {
        return null;
    }

    static final String userCommands =
            "-----------------------------commands-----------------------------\n" +
                    "!help [staff|movienight|admin]         //shows a list of commands\n" +
                    "     aliases: !commands\n" +
                    "!staff                                 //summons moderators to your location\n" +
                    "!stats                                 //shows information about the server \n" +
                    "     aliases: !info, !serverinfo, !server\n" +
                    "!suggest [stuff]                       //pins your suggestion in #suggestion\n" +
                    "     aliases: !idea, !suggestion\n" +
                    "!seesuggestions                        //sends you a list of the current suggestions\n" +
                    "     aliases: !seesuggest, !seeideas\n" +
                    "!show [show name]                      //gives info about a show\n" +
                    "!movie [movie name]                    //gives info about a movie\n" +
                    "!leaderboard                           //shows users with the top 5 xp\n" +
                    "!movienight ping                       //sends movienight info in a pm";

    static final String staffCommands =
            "\n------------------------------staff------------------------------\n" +
                    "!rule [number]                         //posts the rule requested in chat\n" +
                    "!addlog [message]                      //adds a message to the log\n" +
                    "     aliases: !log\n" +
                    "!prune [#] @user                       //deletes a number of messages by a user\n" +
                    "     exclude user to purge all messages (admin only)\n" +
                    "     aliases: !purge\n" +
                    "!mute @user [reason?]                  //mutes user\n" +
                    "!unmute @user                          //unmutes user\n" +
                    "!mute list                             //lists all muted users\n" +
                    "!embedmute @user                       //removes users embed perms\n" +
                    "!unembedmute @user                     //restores users embed perms\n" +
                    "!staffban [add|remove] @user           //removes or adds a user's ability to summon staff\n" +
                    "!ban @user [reason]                    //bans user and logs the action";

    static final String adminCommands =
            "\n------------------------------admin------------------------------\n" +
                    "!addshow [imdbid] [here|channelid]     //adds a new show to the calendar\n" +
                    "!removeshow [imdbid]                   //deletes a show from the calendar\n" +
                    "!showid [here|showname]                //returns possible imdb id for a show\n" +
                    "!announce [announcement]               //sends a public announcement\n" +
                    "!addchannel [name | name | name]       //creates a new channel with the given name\n" +
                    "     aliases: !newchannel\n" +
                    "!resetxp @user                         //resets a users message count\n" +
                    "!amute @user                           //mutes user without log\n" +
                    "!aunmute @user                         //unmutes user without log\n" +
                    "!kick @user [reason]                   //kicks user and logs the action\n" +
                    "!addpchannel [channel mentions]        //makes a channel not auto sort\n" +
                    "!removepchannel [channel mentions]     //removes a channel from the unsort list\n" +
                    "!listpchannels                         //lists all unsortable channels\n" +
                    "!addcommand command response           //adds a custom command with a custom response\n" +
                    "!removecommand command                 //removes a custom command\n" +
                    "!listcommands                          //lists all custom commands";

    static final String movieCommands =
            "\n------------------------------------------------------------------\n" +
                    "!movienight set [pollID] [date]        //posts a link to a the google poll\n" +
                    "!movienight announce [movie]           //deletes poll and announces movie\n" +
                    "!movienight start [rabbitID]           //announces movienight start and links to room";


    @Override
    public void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {

        EmbedBuilder embed = Util.getEmbed();
        embed.withTitle("Commands:");

        List<String> roles = message.getAuthor().getRolesForGuild(guild).stream().map(role -> role.getID()).collect(Collectors.toList());
        for (Command c : MovieBot.registeredCommands) {

            if (c.getDescription() != null) {

                String aliases = "";
                if (c.getAliases() != null) {
                    aliases = "\n**Aliases:**\n" + c.getAliases().toString();
                }

                if (c.getPermissions() == null) {
                    embed.appendField(c.getSyntax(), c.getDescription() + aliases, false);
                } else if (!Collections.disjoint(roles, c.getPermissions())) {
                    embed.appendField(c.getSyntax(), c.getDescription() + aliases, false);
                }

            }

        }

        embed.withFooterText("Staff commands excluded for regular users");

        try {
            Util.sendEmbed(message.getAuthor().getOrCreatePMChannel(), embed.withColor(85, 50, 176).build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Util.deleteMessage(message);

    }

}
