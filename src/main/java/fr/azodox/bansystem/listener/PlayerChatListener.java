package fr.azodox.bansystem.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import fr.azodox.bansystem.config.PlaceHolderParser;
import fr.azodox.bansystem.managers.MuteManager;

public class PlayerChatListener {

    private final BanSystem banSystem;

    public PlayerChatListener(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onChat(PlayerChatEvent e){
        Player p = e.getPlayer();
        MuteManager muteManager = banSystem.getMuteManager();
        muteManager.checkDuration(p.getUniqueId());

        if(muteManager.isMuted(p.getUniqueId())){
            e.setResult(PlayerChatEvent.ChatResult.denied());
            p.sendMessage(PlaceHolderParser.parseMutePlaceHolders(Configs.MESSAGE_CONFIG.YOU_CANNOT_TALK, muteManager, p));
        }
    }
}
