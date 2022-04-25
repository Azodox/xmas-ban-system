package fr.azodox.bansystem.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;

import java.util.UUID;

public class ServerPingListener {
    private final BanSystem banSystem;
    public ServerPingListener(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPing(ProxyPingEvent event) {
        var inboundConnection = event.getConnection();
        var client = banSystem.getMongo().getMongoClient();
        var accounts = client.getDatabase(Configs.DEFAULT_CONFIG.MONGODB_DATABASE).getCollection("accounts");

        for (Document document : accounts.find()) {
            if (document.getString("last-ip").split(":")[0].equals(inboundConnection.getRemoteAddress().getHostString())) {
                if (banSystem.getBanManager().isBanned(UUID.fromString(document.getString("uuid")))) {
                    event.setPing(ServerPing.builder()
                            .description(Component.text("Vous êtes banni du serveur !").color(NamedTextColor.RED))
                            .onlinePlayers(-1)
                            .maximumPlayers(0)
                            .version(new ServerPing.Version(-1, ""))
                            .build());
                }
            }
        }
    }
}
