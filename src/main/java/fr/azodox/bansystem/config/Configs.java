package fr.azodox.bansystem.config;

public final class Configs {

    public static MessageConfig MESSAGE_CONFIG = new MessageConfig();
    public static DefaultConfig DEFAULT_CONFIG = new DefaultConfig();

    public static void reload(){
        MESSAGE_CONFIG = new MessageConfig();
        DEFAULT_CONFIG = new DefaultConfig();
    }
}
