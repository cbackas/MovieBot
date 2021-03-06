package cback;

import cback.eventFunctions.*;
import cback.commands.*;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MovieBot {

    private static MovieBot instance;
    private static IDiscordClient client;

    private TraktManager traktManager;
    private static ConfigManager configManager;
    private static CommandManager commandManager;
    private Scheduler scheduler;

    public static ArrayList<Long> messageCache = new ArrayList<>();

    public  static List<Command> registeredCommands = new ArrayList<>();
    static public String prefix = "!";
    public List<String> prefixes = new ArrayList<>();
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?s)^!([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);

    public static final long CBACK_USR_ID = 73416411443113984L;
    public static final long HOMESERVER_GLD_ID = 256248900124540929L;

    public static final long SPOILERCHAT_CAT_ID = 355935097502826496L;

    public static final long INFO_CH_ID = 263185370424803328L;
    public static final long ANNOUNCEMENT_CH_ID = 263185392818323466L;
    public static final long GENERAL_CH_ID = 256248900124540929L;
    public static final long MESSAGELOG_CH_ID = 337666816383909889L;
    public static final long SERVERLOG_CH_ID = 261737182543216640L;
    public static final long STAFF_CH_ID = 226433456060497920L;
    public static final long DEV_CH_ID = 268510274858778624L;
    public static final long MUTED_ROLE_ID = 266655441449254914L;

    public static final long ERRORLOG_CH_ID = 346104666796589056L;
    public static final long BOTLOG_CH_ID = 346483682376286208L;
    public static final long BOTPM_CH_ID = 346104720903110656L;

    private long startTime;

    public static void main(String[] args) {
        new MovieBot();
    }

    public MovieBot() {

        instance = this;

        //instantiate config manager first as connect() relies on tokens
        configManager = new ConfigManager(this);
        commandManager = new CommandManager(this);

        prefixes.add(getPrefix());
        prefixes.add("t!");
        prefixes.add("!g");
        prefixes.add("--");
        prefixes.add(".");
        prefixes.add("?");

        connect();
        client.getDispatcher().registerListener(this);
        client.getDispatcher().registerListener(new ChannelChange(this));
        client.getDispatcher().registerListener(new MemberChange(this));
        client.getDispatcher().registerListener(new MessageChange(this));

        traktManager = new TraktManager(this);
        scheduler = new Scheduler(this);

        registerAllCommands();
    }

    private void connect() {
        //don't load external modules and don't attempt to create modules folder
        Configuration.LOAD_EXTERNAL_MODULES = false;

        Optional<String> token = configManager.getTokenValue("botToken");
        if (!token.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your bot's token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token.get());
        clientBuilder.setMaxReconnectAttempts(5);
        try {
            client = clientBuilder.login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    /*
     * Message Central Choo Choo
     */
    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) return; //ignore bot messages
        IMessage message = event.getMessage();
        IGuild guild = null;
        boolean isPrivate = message.getChannel().isPrivate();
        if (!isPrivate) guild = message.getGuild();
        String text = message.getContent();
        Matcher matcher = COMMAND_PATTERN.matcher(text);
        if (matcher.matches()) {
            String baseCommand = matcher.group(1).toLowerCase();
            Optional<Command> command = registeredCommands.stream()
                    .filter(com -> com.getName().equalsIgnoreCase(baseCommand) || (com.getAliases() != null && com.getAliases().contains(baseCommand)))
                    .findAny();
            if (command.isPresent()) {
                Command cCommand = command.get();

                if (cCommand.getDescription() != null || message.getAuthor().getLongID() == CBACK_USR_ID) {
                    System.out.println("@" + message.getAuthor().getName() + " issued \"" + text + "\" in " +
                            (isPrivate ? ("@" + message.getAuthor().getName()) : guild.getName()));

                    String args = matcher.group(2);
                    String[] argsArr = args.isEmpty() ? new String[0] : args.split(" ");

                    List<Long> roleIDs = message.getAuthor().getRolesForGuild(guild).stream().map(role -> role.getLongID()).collect(Collectors.toList());

                    IUser author = message.getAuthor();
                    String content = message.getContent();

                    /**
                     * If user has permission to run the command: Command executes and botlogs
                     */
                    //message.getChannel().setTypingStatus(true);
                    if (cCommand.getPermissions() == null || !Collections.disjoint(roleIDs, cCommand.getPermissions())) {
                        Util.botLog(message);
                        cCommand.execute(message, content, argsArr, author, guild, roleIDs, isPrivate, client, this);
                        //message.getChannel().setTypingStatus(false);
                    } else {
                        Util.simpleEmbed(message.getChannel(), "You don't have permission to perform this command.");
                        //message.getChannel().setTypingStatus(false);
                    }
                }
            } else if (commandManager.getCommandValue(baseCommand) != null) {

                String response = commandManager.getCommandValue(baseCommand);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("``" + message.getAuthor().getDisplayName(guild) + "``\n").append(response);

                Util.sendMessage(message.getChannel(), stringBuilder.toString());

                Util.deleteMessage(message);
            }
            /**
             * Forwards the random stuff people PM to the bot - to me
             */
        } else if (message.getChannel().isPrivate()) {
            EmbedObject embed = Util.buildBotPMEmbed(message, 1);
            Util.sendEmbed(client.getChannelByID(BOTPM_CH_ID), embed);
        } else {
            //below here are just regular chat messages
            censorMessages(message);

            /**
             * Deletes messages/bans users for using too many @ mentions
             */
            boolean staffMember = message.getAuthor().hasRole(message.getClient().getRoleByID(MovieRoles.STAFF.id));
            if (!staffMember) {
                if (Util.mentionsCount(message.getContent()) > 10) {
                    try {
                        guild.banUser(message.getAuthor(), "Mentioned more than 10 users in a message. Appeal at https://www.reddit.com/r/LoungeBan/", 0);
                        Util.simpleEmbed(message.getChannel(), message.getAuthor().getDisplayName(guild) + " was just banned for mentioning more than 10 users.");
                        Util.sendLog(message, "Banned " + message.getAuthor().getName() + "\n**Reason:** Doing too many @ mentions", Color.red);
                    } catch (Exception e) {
                        Util.reportHome(e);
                    }
                } else if (Util.mentionsCount(message.getContent()) > 5) {
                    Util.deleteMessage(message);
                }
            }

            /**
             * Messages containing my name go to botpms now too cuz im watching
             */
            if (message.getContent().toLowerCase().contains("cback")) {
                EmbedObject embed = Util.buildBotPMEmbed(message, 2);
                Util.sendEmbed(client.getChannelByID(BOTPM_CH_ID), embed);
            }
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("Logged in.");
        client = event.getClient();

        //Set status
        client.changePresence(StatusType.ONLINE, ActivityType.WATCHING,"all of your messages. Type " + prefix + "help");

        startTime = System.currentTimeMillis();
    }

    public TraktManager getTraktManager() {
        return traktManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static IDiscordClient getClient() {
        return client;
    }

    public static MovieBot getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static IGuild getHomeGuild() { return client.getGuildByID(Long.parseLong(configManager.getConfigValue("HOMESERVER_ID")));}

    private void registerAllCommands() {
        new Reflections("cback.commands").getSubTypesOf(Command.class).forEach(commandImpl -> {
            try {
                Command command = commandImpl.newInstance();
                Optional<Command> existingCommand = registeredCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findAny();
                if (!existingCommand.isPresent()) {
                    registeredCommands.add(command);
                    System.out.println("Registered command: " + command.getName());
                } else {
                    System.out.println("Attempted to register two commands with the same name: " + existingCommand.get().getName());
                    System.out.println("Existing: " + existingCommand.get().getClass().getName());
                    System.out.println("Attempted: " + commandImpl.getName());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public String getUptime() {
        long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600);
        return (hours < 10 ? "0" + hours : hours) + "h " + (minutes < 10 ? "0" + minutes : minutes) + "m " + (seconds < 10 ? "0" + seconds : seconds) + "s";
    }

    /**
     * Checks for dirty words :o
     */
    public void censorMessages(IMessage message) {
        boolean homeGuild = message.getGuild().getLongID() == MovieBot.HOMESERVER_GLD_ID;
        boolean staffChannel = message.getChannel().getCategory().getLongID() == 355935127618191370L || message.getChannel().getCategory().getLongID() == 355934857047834626L;
        boolean staffMember = message.getAuthor().hasRole(message.getClient().getRoleByID(MovieRoles.STAFF.id));
        if (homeGuild && !staffChannel && !staffMember) {
            List<String> bannedWords = MovieBot.getInstance().getConfigManager().getConfigArray("bannedWords");
            String content = message.getFormattedContent().toLowerCase();

            String word = "";
            Boolean tripped = false;
            for (String w : bannedWords) {
                if (content.matches("\\n?.*\\b\\n?" + w + "\\n?\\b.*\\n?.*") || content.matches("\\n?.*\\b\\n?" + w + "s\\n?\\b.*\\n?.*")) {
                    tripped = true;
                    word = w;
                    break;
                }
            }
            if (tripped) {

                IUser author = message.getAuthor();

                EmbedBuilder bld = new EmbedBuilder();
                bld
                        .withAuthorIcon(author.getAvatarURL())
                        .withAuthorName(Util.getTag(author))
                        .withDesc(message.getFormattedContent())
                        .withTimestamp(System.currentTimeMillis())
                        .withFooterText("Auto-deleted from #" + message.getChannel().getName());

                Util.sendEmbed(message.getGuild().getChannelByID(MESSAGELOG_CH_ID), bld.withColor(Util.getBotColor()).build());

                StringBuilder sBld = new StringBuilder().append("Your message has been automatically removed for containing a banned word. If this is an error, message a staff member.");
                if (!word.isEmpty()) {
                    sBld
                            .append("\n\n")
                            .append(word);
                }
                Util.sendPrivateEmbed(author, sBld.toString());

                messageCache.add(message.getLongID());
                Util.deleteMessage(message);
            }
        }
    }

}
