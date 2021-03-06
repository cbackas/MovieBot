package cback.commands;

import cback.MovieBot;
import cback.Util;
import com.uwetrottmann.trakt5.entities.Movie;
import org.threeten.bp.format.DateTimeFormatter;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSearchMovies implements Command {
    @Override
    public String getName() {
        return "movie";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "movie [movie name]";
    }

    @Override
    public String getDescription() {
        return "Searches trakt.tv for the provided movie. Sometimes it works, sometimes it doesn't.";
    }

    @Override
    public List<Long> getPermissions() {
        return null;
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, MovieBot bot) {
        String movieName = Arrays.stream(args).collect(Collectors.joining(" "));

        IMessage initialMessage = Util.simpleEmbed(message.getChannel(), "Searching <https://trakt.tv/> for " + movieName + " ...");

        Movie movieData = bot.getTraktManager().movieSummaryFromName(movieName);
        if (movieData != null) {
            String title = "**" + movieData.title + " (" + Integer.toString(movieData.year) + ")**";
            String overview = movieData.overview;
            String premier = movieData.released.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            String rating = movieData.certification;
            String runtime = Integer.toString(movieData.runtime);
            String country = movieData.language;
            String homepage = "<https://trakt.tv/movies/" + movieData.ids.slug + ">\n<http://www.imdb.com/title/" + movieData.ids.imdb + ">";

            EmbedBuilder embed = Util.getEmbed(message.getAuthor());

            if (movieData.tagline != null) {
                embed.withTitle(movieData.tagline);
            }

            embed.withTitle(title);
            embed.withDescription(overview);
            embed.appendField("References:", homepage, false);
            embed.appendField("PREMIERED:", premier, true);
            embed.appendField("RUNTIME:", runtime, true);
            embed.appendField("RATED:", rating, true);
            embed.appendField("LANGUAGE:", country.toUpperCase(), true);
            embed.appendField("GENRES:", String.join(", ", movieData.genres), true);

            initialMessage.edit("Found it!", embed.withColor(Util.getBotColor()).build());
        } else {
            initialMessage.edit("Oops...", new EmbedBuilder().withDesc("Error: Movie not found").withColor(Util.getBotColor()).build());
            Util.simpleEmbed(client.getChannelByID(MovieBot.ERRORLOG_CH_ID), "Couldn't find movie " + movieName + " in " + guild.getName() + "/" + message.getChannel().getName());
        }
    }

}
