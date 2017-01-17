package cback.commands;

import cback.MovieBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;

public interface Command {
    String getName();

    List<String> getAliases();

    String getSyntax();

    String getDescription();

    List<String> getPermissions();

    void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate);
}

