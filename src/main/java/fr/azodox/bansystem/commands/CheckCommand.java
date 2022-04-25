package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.managers.BanManager;
import fr.azodox.bansystem.managers.MuteManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckCommand implements SimpleCommand {

    private final BanSystem banSystem;

    public CheckCommand(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Override
    public void execute(Invocation invocation) {
        var sender = invocation.source();
        var args = invocation.arguments();
        if(args.length != 1){
            sender.sendMessage(Component.text("/check <joueur>").color(NamedTextColor.RED));
            return;
        }

        String targetName = args[0];

        Document document = banSystem.getMongoUtil().getByName(targetName);
        if(document == null) {
            sender.sendMessage(Component.text("Ce joueur ne s'est jamais connecté au serveur !").color(NamedTextColor.RED));
            return;
        }

        List<String> msg = new ArrayList<>();

        UUID uuid = UUID.fromString(document.getString("uuid"));

        BanManager banManager = banSystem.getBanManager();
        banManager.checkDuration(uuid);

        MuteManager muteManager = banSystem.getMuteManager();
        muteManager.checkDuration(uuid);

        msg.add("§7§m                                                    ");
        msg.add("§ePseudo : §b" + args[0]);
        msg.add("§eUUID : §b" + uuid);
        msg.add("§eBanni : " + (banManager.isBanned(uuid) ? "§a✔" : "§c✖"));

        if(banManager.isBanned(uuid)){
            msg.add("\n§aBan :");
            msg.add("§bPar : §c" + banManager.getAuthor(uuid));
            msg.add("§bRaison : §c" + banManager.getReason(uuid));
            msg.add("§bTemps restant : §f" + banManager.getTimeLeft(uuid));
        }

        msg.add("\n§eMute : " + (muteManager.isMuted(uuid) ? "§a✔" : "§c✖"));

        if(muteManager.isMuted(uuid)){
            msg.add("\n§aMute :");
            msg.add("§bPar : §c" + muteManager.getAuthor(uuid));
            msg.add("§bRaison : §c" + muteManager.getReason(uuid));
            msg.add("§bTemps restant : §f" + muteManager.getTimeLeft(uuid));
        }
        msg.add("§7§m                                                    ");

        for(String string : msg){
            sender.sendMessage(Component.text(string));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
