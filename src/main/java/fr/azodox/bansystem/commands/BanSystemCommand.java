package fr.azodox.bansystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import fr.azodox.bansystem.config.Configs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class BanSystemCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();
        var args = invocation.arguments();

        if(args.length == 0) {
            source.sendMessage(Component.text("/bansystem <reload>").color(NamedTextColor.RED));
            return;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            source.sendMessage(Component.text("Rechargement des configs...").color(NamedTextColor.GREEN));
            Configs.reload();
            source.sendMessage(Component.text("Configs rechargées avec succès !").color(NamedTextColor.GREEN));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("xmas.command.bansystem");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }
}
