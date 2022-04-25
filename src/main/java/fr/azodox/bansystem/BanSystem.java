package fr.azodox.bansystem;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.azodox.bansystem.commands.*;
import fr.azodox.bansystem.listener.PlayerChatListener;
import fr.azodox.bansystem.listener.PlayerConnectionListener;
import fr.azodox.bansystem.listener.ServerPingListener;
import fr.azodox.bansystem.managers.BanManager;
import fr.azodox.bansystem.managers.MuteManager;
import fr.azodox.bansystem.mongo.Mongo;
import fr.azodox.bansystem.utils.MongoUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Plugin(id = "ban-system", name = "BanSystem", version = "0.1.0-xmas", authors = {"Azodox_"})
public class BanSystem {

    public final static String DB_NAME = "xmas";

    private Mongo mongo;
    private MongoUtil mongoUtil;

    private BanManager banManager;
    private MuteManager muteManager;

    private static BanSystem instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public BanSystem(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event){
        createFiles();

        mongo = new Mongo();
        mongoUtil = new MongoUtil(this);

        banManager = new BanManager(this);
        muteManager = new MuteManager(this);

        server.getEventManager().register(this, new PlayerChatListener(this));
        server.getEventManager().register(this, new PlayerConnectionListener(this));
        server.getEventManager().register(this, new ServerPingListener(this));
        registerCommands();

        logger.info("Enabled!");
        logger.info("Ready to work!");
    }

    private void registerCommands(){
        var commandManager = server.getCommandManager();
        var ban = commandManager.metaBuilder("ban").build();
        var unban = commandManager.metaBuilder("unban").build();
        var check = commandManager.metaBuilder("check").build();
        var mute = commandManager.metaBuilder("mute").build();
        var unmute = commandManager.metaBuilder("unmute").build();
        var kick = commandManager.metaBuilder("kick").build();
        var banSystemMeta = commandManager.metaBuilder("bansystem").build();

        commandManager.register(ban, new BanCommand(this));
        commandManager.register(unban, new UnBanCommand(this));
        commandManager.register(check, new CheckCommand(this));
        commandManager.register(mute, new MuteCommand(this));
        commandManager.register(unmute, new UnMuteCommand(this));
        commandManager.register(kick, new KickCommand(this));
        commandManager.register(banSystemMeta, new BanSystemCommand());
    }

    /**
     * Create all files needed
     */
    private void createFiles() {
        if(!dataDirectory.toFile().exists()){
            dataDirectory.toFile().mkdirs();
        }

        logger.info("Creating all files...");
        File[] files = new File[] { new File("config.toml"), new File("messages.toml") };
        for (File file : files) {
            createFileFromOther(dataDirectory.toFile(), file.getName(), file);
        }
        logger.info("Created all configurations files (TOML)!");
    }

    /**
     * Create a file in a folder from another one (example: resources folder to -> a folder)
     * @param folder : The folder which contains the file
     * @param name : The name of the file created based on the original
     * @param other : The original file, the "from" file.
     */
    private void createFileFromOther(File folder, String name, File other){
        File file = new File(folder, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if(!FileUtils.contentEquals(file, other) && file.length() != 0){
                logger.info("File '" + other.getPath() + "' content doesn't equals to '" + other.getPath() + "' and is not empty, skipping files copy.");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            logger.info(other.getName());
            InputStream in = getClass().getResourceAsStream("/" + other.getName());
            FileOutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int n;

            while ((n = in.read(buf)) >= 0){
                out.write(buf, 0, n);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Created file '" + name + "' in [" + file.getPath() + "] from '" + other.getName() + "' in [" + folder.getPath() + "]");
    }

    public Mongo getMongo() {
        return mongo;
    }

    public MongoUtil getMongoUtil() {
        return mongoUtil;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public static BanSystem getInstance() {
        return instance;
    }
}
