package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import fr.azodox.bansystem.config.PlaceHolderParser;
import fr.azodox.bansystem.managers.BanManager;
import fr.azodox.bansystem.utils.TimeUnit;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class BanCommand implements SimpleCommand {

    private final BanSystem banSystem;

    public BanCommand(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    @Override
    public void execute(Invocation invocation) {
        var sender = invocation.source();
        var args = invocation.arguments();
        var senderId = sender instanceof Player ? sender.get(Identity.NAME).orElseThrow() : "CONSOLE";

        if(args.length < 3){
            helpMessage(sender);
            return;
        }

        final String targetName = args[0];

        Document document = banSystem.getMongoUtil().getByName(targetName);
        if(document == null) {
            sender.sendMessage(Component.text("Ce joueur ne s'est jamais connecté au serveur !").color(NamedTextColor.RED));
            return;
        }

        final UUID uuid = UUID.fromString(document.getString("uuid"));
        Player target = banSystem.getServer().getPlayer(uuid).orElse(null);
        if(target != null){
            if(document.getBoolean("ban-bypass") != null && document.getBoolean("ban-bypass")){
                if(sender instanceof Player player){
                    if(document.getBoolean("ban-bypass-playerBypass") != null && !document.getBoolean("ban-bypass-playerBypass")){
                        player.sendMessage(Component.text("Erreur : Vous ne pouvez pas bannir ce joueur !").color(NamedTextColor.RED));
                        return;
                    }
                }
            }
        }

        BanManager banManager = banSystem.getBanManager();
        banManager.checkDuration(uuid);

        if(banManager.isBanned(uuid)){
            sender.sendMessage(PlaceHolderParser.parseBanPlaceHolders(Configs.MESSAGE_CONFIG.ALREADY_BANNED, banManager, banSystem.getServer().getPlayer(uuid).orElseThrow()));
            return;
        }

        String reason = "";
        for(int i = 2; i < args.length; i++){
            reason += args[i] + " ";
        }

        if(args[1].equalsIgnoreCase("perm")){
            banManager.ban(senderId, uuid, -1, reason);
            sender.sendMessage(PlaceHolderParser.parseBanPlaceHolders(Configs.MESSAGE_CONFIG.YOU_HAVE_BAN_PERM, banManager, banSystem.getServer().getPlayer(uuid).orElseThrow()));
            return;
        }

        if(!args[1].contains(":")){
            helpMessage(sender);
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(args[1].split(":")[0]);
        } catch(NumberFormatException e){
            sender.sendMessage(Component.text("§cLa valeur 'durée' doit être un nombre !"));
            return;
        }

        if(!TimeUnit.existFromShortcut(args[1].split(":")[1])){
            sender.sendMessage(Component.text("§cCette unité de temps n'existe pas !"));
            for(TimeUnit units : TimeUnit.values()){
                sender.sendMessage(Component.text("§b" + units.getName() + " §f: §e" + units.getShortcut()));
            }
            return;
        }

        TimeUnit unit = TimeUnit.getFromShortcut(args[1].split(":")[1]);
        long banTime = unit.getToSecond() * duration;

        banManager.ban(senderId, uuid, banTime, reason);
        sender.sendMessage(
                PlaceHolderParser.parseTimestampPlaceHolders(Configs.MESSAGE_CONFIG.YOU_HAVE_BAN_X_TIME, duration, unit, reason, targetName)
        );
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    public void helpMessage(CommandSource sender){
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Configs.MESSAGE_CONFIG.BAN_HELP));
    }
}
