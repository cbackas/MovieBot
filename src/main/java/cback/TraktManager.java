package cback;

import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.*;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.enums.Type;
import retrofit2.Response;
;
import java.util.List;
import java.util.Optional;

public class TraktManager {

    private TraktV2 trakt;
    private MovieBot bot;

    public TraktManager(MovieBot bot) {
        this.bot = bot;

        Optional<String> traktToken = bot.getConfigManager().getTokenValue("traktToken");
        if (!traktToken.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your Trakt token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }
        trakt = new TraktV2(traktToken.get());
    }

    public Show showSummaryFromName(String showName) {
        try {
            //Response<List<SearchResult>> search = trakt.search().textQuery(showName, Type.SHOW, null, 1, 1).execute();
            Response<List<SearchResult>> search = trakt.search().textQuery(Type.SHOW, showName, null, null, null, null, null, null, Extended.FULL, 1, 1).execute();
            if (search.isSuccessful() && !search.body().isEmpty()) {
                Response<Show> show = trakt.shows().summary(search.body().get(0).show.ids.imdb, Extended.FULL).execute();
                if (show.isSuccessful()) {
                    return show.body();
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Movie movieSummaryFromName(String movieName) {
        try {
            Response<List<SearchResult>> search = trakt.search().textQuery(Type.MOVIE, movieName, null, null, null, null, null, null, Extended.FULL, 1, 1).execute();
            if (search.isSuccessful() && !search.body().isEmpty()) {
                Response<Movie> movie = trakt.movies().summary(search.body().get(0).movie.ids.imdb, Extended.FULL).execute();
                if (movie.isSuccessful()) {
                    return movie.body();
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}