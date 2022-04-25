package fr.azodox.bansystem.config;

import java.util.Objects;

public class DefaultConfig extends TomlConfig{

    protected DefaultConfig() {
        super("config");
    }

    public final String MONGODB_HOST = Objects.requireNonNull(getConfig().getString("MongoDB.host"));
    public final int MONGODB_PORT = getConfig().getLong("MongoDB.port").intValue();
    public final String MONGODB_USERNAME = Objects.requireNonNull(getConfig().getString("MongoDB.username"));
    public final String MONGODB_PASSWORD = Objects.requireNonNull(getConfig().getString("MongoDB.password"));
    public final String MONGODB_DATABASE = Objects.requireNonNull(getConfig().getString("MongoDB.database"));
    public final String MONGODB_AUTH_DATABASE = Objects.requireNonNull(getConfig().getString("MongoDB.authDatabase"));
}
