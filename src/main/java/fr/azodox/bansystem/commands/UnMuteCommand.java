package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.managers.MuteManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class UnMuteCommand implements SimpleCommand {

    private final BanSystem banSystem;

    public UnMuteCommand(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Override
    public void execute(Invocation invocation) {
        var sender = invocation.source();
        var args = invocation.arguments();
        if (args.length != 1) {
            sender.sendMessage(Component.text("/unmute <joueur>").color(NamedTextColor.RED));
            return;
        }

        String targetName = args[0];

        Document document = banSystem.getMongoUtil().getByName(targetName);
        if(document == null) {
            sender.sendMessage(Component.text("Ce joueur ne s'est jamais connecté au serveur !").color(NamedTextColor.RED));
            return;
        }

        UUID uuid = UUID.fromString(document.getString("uuid"));

        MuteManager muteManager = banSystem.getMuteManager();
        muteManager.checkDuration(uuid);

        if (!muteManager.isMuted(uuid)) {
            sender.sendMessage(Component.text("Ce joueur n'est pas mute !").color(NamedTextColor.RED));
            return;
        }

        muteManager.unmute(uuid);
        sender.sendMessage(Component.text("§aVous avez unmute §6" + targetName));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
