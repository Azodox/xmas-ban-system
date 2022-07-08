package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.managers.BanManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class UnBanCommand implements SimpleCommand {

    private final BanSystem banSystem;

    public UnBanCommand(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Override
    public void execute(Invocation invocation) {
        var sender = invocation.source();
        var args = invocation.arguments();
        if(args.length != 1){
            sender.sendMessage(Component.text("/unban <joueur>").color(NamedTextColor.RED));
            return;
        }

        String targetName = args[0];

        Document document = banSystem.getMongoUtil().getByName(targetName);
        if(document == null) {
            sender.sendMessage(Component.text("Ce joueur ne s'est jamais connecté au serveur !").color(NamedTextColor.RED));
            return;
        }

        UUID uuid = UUID.fromString(document.getString("uuid"));

        BanManager banManager = banSystem.getBanManager();
        banManager.checkDuration(uuid);

        if(!banManager.isBanned(uuid)){
            sender.sendMessage(Component.text("Ce joueur n'est pas banni !").color(NamedTextColor.RED));
            return;
        }

        banManager.unban(uuid);
        sender.sendMessage(Component.text("§aVous avez débanni §6" + targetName));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("xmas.moderation.unban");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
