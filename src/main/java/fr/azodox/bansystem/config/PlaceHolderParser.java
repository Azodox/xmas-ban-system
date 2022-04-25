package fr.azodox.bansystem.config;

import fr.azodox.bansystem.managers.BanManager;
import fr.azodox.bansystem.managers.MuteManager;
import fr.azodox.bansystem.utils.TimeUnit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Objects;

public final class PlaceHolderParser {

    public static TextComponent parseBanPlaceHolders(String s, BanManager banManager, Audience player){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s.replace("%player%", player.get(Identity.NAME).orElse(null))
                .replace("%remaining%", banManager.getTimeLeft(player.get(Identity.UUID).orElse(null)))
                .replace("%author%", banManager.getAuthor(player.get(Identity.UUID).orElse(null)))
                .replace("%reason%", banManager.getReason(player.get(Identity.UUID).orElse(null))));
    }

    public static TextComponent parseMutePlaceHolders(String s, MuteManager muteManager, Audience player){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s
                .replace("%player%", Objects.requireNonNull(player.get(Identity.NAME).orElse(null)))
                .replace("%remaining%", muteManager.getTimeLeft(player.get(Identity.UUID).orElse(null)))
                .replace("%author%", muteManager.getAuthor(player.get(Identity.UUID).orElse(null)))
                .replace("%reason%", muteManager.getReason(player.get(Identity.UUID).orElse(null))));
    }

    /**
     * Replace placeholders from messages containing durations
     * @param s : the message
     * @param duration : the duration
     * @param timeUnit : the time unit instance
     * @param reason ! the reason
     * @param playerName : the target name
     * @return parsed string
     */
    public static TextComponent parseTimestampPlaceHolders(String s, long duration, TimeUnit timeUnit, String reason, String playerName){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s
                .replace("%player%", playerName)
                .replace("%duration%", duration + " " + timeUnit.getName())
                .replace("%reason%", reason));
    }

}
