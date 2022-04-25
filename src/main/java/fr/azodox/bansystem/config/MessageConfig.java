package fr.azodox.bansystem.config;

public class MessageConfig extends TomlConfig {

    public MessageConfig() {
        super("messages");
    }

    public final String CONNECTION_FORBIDDEN = parseStringFromArray("Ban.connection-forbidden");
    public final String ALREADY_BANNED = parseStringFromArray("Ban.already-banned");
    public final String BAN_KICKED = parseStringFromArray("Ban.ban-kicked");
    public final String YOU_HAVE_BAN_PERM = parseStringFromArray("Ban.you-have-ban-perm");

    public final String YOU_CANNOT_TALK = parseStringFromArray("Mute.you-cannot-talk");
    public final String ALREADY_MUTED = parseStringFromArray("Mute.already-muted");
    public final String YOU_HAVE_BEEN_MUTED = parseStringFromArray("Mute.you-have-been-muted");
    public final String YOU_HAVE_MUTE_PERM = parseStringFromArray("Mute.you-have-mute-perm");

    public final String YOU_HAVE_BAN_X_TIME = parseStringFromArray("Timestamp.Messages.you-have-ban-x-time");
    public final String YOU_HAVE_MUTE_X_TIME = parseStringFromArray("Timestamp.Messages.you-have-mute-x-time");

    public final String BAN_HELP = parseStringFromArray("Messages.ban-help");
    public final String MUTE_HELP = parseStringFromArray("Messages.mute-help");
}
