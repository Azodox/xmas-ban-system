package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import fr.azodox.bansystem.config.PlaceHolderParser;
import fr.azodox.bansystem.managers.MuteManager;
import fr.azodox.bansystem.utils.TimeUnit;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class MuteCommand implements SimpleCommand {

    private final BanSystem banSystem;

    public MuteCommand(BanSystem banSystem) {
        this.banSystem = banSystem;
    }

    public void helpMessage(CommandSource sender){
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(Configs.MESSAGE_CONFIG.MUTE_HELP));
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

        String targetName = args[0];

        Document document = banSystem.getMongoUtil().getByName(targetName);
        if(document == null) {
            sender.sendMessage(Component.text("Ce joueur ne s'est jamais connecté au serveur !").color(NamedTextColor.RED));
            return;
        }

        UUID uuid = UUID.fromString(document.getString("uuid"));

        if(document.getBoolean("mute-bypass") != null && document.getBoolean("mute-bypass")){
            if(document.getBoolean("mute-bypass-playerBypass") != null && document.getBoolean("mute-bypass-playerBypass")){
                sender.sendMessage(Component.text("Erreur : Vous ne pouvez pas mute ce joueur !").color(NamedTextColor.RED));
                return;
            }
        }

        MuteManager muteManager = banSystem.getMuteManager();
        muteManager.checkDuration(uuid);

        if(muteManager.isMuted(uuid)){
            sender.sendMessage(Component.text("Ce joueur est déjà mute !").color(NamedTextColor.RED));
            return;
        }

        String reason = "";
        for(int i = 2; i < args.length; i++){
            reason += args[i] + " ";
        }

        if(args[1].equalsIgnoreCase("perm")){
            muteManager.mute(senderId, uuid, -1, reason);
            sender.sendMessage(PlaceHolderParser.parseMutePlaceHolders(Configs.MESSAGE_CONFIG.YOU_HAVE_MUTE_PERM, muteManager, banSystem.getServer().getPlayer(uuid).orElseThrow()));
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
            sender.sendMessage(Component.text("La valeur 'durée' doit être un nombre !").color(NamedTextColor.RED));
            return;
        }

        if(!TimeUnit.existFromShortcut(args[1].split(":")[1])){
            sender.sendMessage(Component.text("Cette unité de temps n'existe pas !").color(NamedTextColor.RED));
            for(TimeUnit units : TimeUnit.values()){
                sender.sendMessage(Component.text("§b" + units.getName() + " §f: §e" + units.getShortcut()));
            }
            return;
        }

        TimeUnit unit = TimeUnit.getFromShortcut(args[1].split(":")[1]);
        long muteTime = unit.getToSecond() * duration;

        muteManager.mute(senderId, uuid, muteTime, reason);
        sender.sendMessage(PlaceHolderParser.parseTimestampPlaceHolders(Configs.MESSAGE_CONFIG.YOU_HAVE_MUTE_X_TIME, duration, unit, reason, targetName));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
