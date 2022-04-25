package fr.azodox.bansystem.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import fr.azodox.bansystem.config.PlaceHolderParser;
import fr.azodox.bansystem.managers.BanManager;

public class PlayerConnectionListener {

    private final BanSystem banSystem;

    public PlayerConnectionListener(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent e){
        Player player = e.getPlayer();
        BanManager banManager = banSystem.getBanManager();
        banManager.checkDuration(player.getUniqueId());

        if(banManager.isBanned(player.getUniqueId())){
            e.setResult(ResultedEvent.ComponentResult.denied(
                    PlaceHolderParser.parseBanPlaceHolders(Configs.MESSAGE_CONFIG.CONNECTION_FORBIDDEN, banManager, player)));

        }
    }
}
