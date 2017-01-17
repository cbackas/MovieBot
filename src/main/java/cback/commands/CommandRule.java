package cback.commands;

import cback.Rules;
import cback.MovieBot;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;


public class CommandRule implements Command {
    @Override
    public String getName() {
        return "rule";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getPermissions() {
        return null;
    }

    @Override
    public void execute(MovieBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (Util.permissionCheck(message, "Staff")) {
            Util.botLog(message);

            if (args.length == 1) {

                String ruleNumber = args[0];
                Rules rule = Rules.getRule(ruleNumber);

                if (rule != null) {
                    Util.sendMessage(message.getChannel(), rule.fullRule);
                } else {
                    Util.sendMessage(message.getChannel(), "Rule not found");
                }

            } else {
                Util.sendMessage(message.getChannel(), "Too many arguments");
            }

        }
    }

}
