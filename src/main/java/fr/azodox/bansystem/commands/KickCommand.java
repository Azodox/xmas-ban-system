package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.azodox.bansystem.BanSystem;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class KickCommand implements SimpleCommand {

    private final BanSystem banSystem;

    public KickCommand(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Override
    public void execute(Invocation invocation) {
        var sender = invocation.source();
        var args = invocation.arguments();
        var senderId = sender instanceof Player ? sender.get(Identity.NAME).orElseThrow() : "CONSOLE";
        if(args.length == 0){
            sender.sendMessage(Component.text("Erreur : /kick [joueur][message]").color(NamedTextColor.RED));
            return;
        }

        if(args.length == 1){
            sender.sendMessage(Component.text("Erreur : /kick [joueur][message]").color(NamedTextColor.RED));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(String part : args){
            if(!part.equals(args[0])) {
                sb.append(part).append(" ");
            }
        }

        var optionalTarget = banSystem.getServer().getPlayer(args[0]);
        if(optionalTarget.isEmpty()){
            sender.sendMessage(Component.text("Erreur : Ce joueur n'existe pas").color(NamedTextColor.RED));
            return;
        }

        var target = optionalTarget.get();

        target.disconnect(Component.text("§6" + senderId + " §evous a kick : §a" + sb));
        sender.sendMessage(Component.text("§8§l» §aOpération réussie avec succès."));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
