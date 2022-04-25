package fr.azodox.bansystem.managers;

import fr.azodox.bansystem.BanSystem;
import fr.azodox.bansystem.config.Configs;
import fr.azodox.bansystem.config.PlaceHolderParser;
import fr.azodox.bansystem.utils.MongoUtil;
import fr.azodox.bansystem.utils.TimeUnit;
import org.bson.Document;

import java.util.UUID;

public class MuteManager {

    private final MongoUtil mongoUtil;
    private final BanSystem banSystem;

    public MuteManager(BanSystem banSystem) {
        this.mongoUtil = banSystem.getMongoUtil();
        this.banSystem = banSystem;
    }

    /**
     * Mute someone
     * @param author : Moderator's uuid
     * @param uuid : Muted player's uuid
     * @param endInSeconds : End of the mute in seconds (if -1 = permanent)
     * @param reason : The mute's reason
     */
    public void mute(String author, UUID uuid, long endInSeconds, String reason){
        if(isMuted(uuid)) return;

        long endToMillis = endInSeconds * 1000;
        long end = endToMillis + System.currentTimeMillis();

        if(endInSeconds == -1){
            end = -1;
        }

        mongoUtil.getMuted().insertOne(
                new Document("uuid", uuid.toString())
                .append("end", end)
                .append("reason", reason)
                .append("author", author)
        );
        
        if(banSystem.getServer().getPlayer(uuid).isPresent()){
            var target = banSystem.getServer().getPlayer(uuid).get();
            target.sendMessage(PlaceHolderParser.parseMutePlaceHolders(Configs.MESSAGE_CONFIG.YOU_HAVE_BEEN_MUTED, this, target));
        }
    }

    public void unmute(UUID uuid){
        if(!isMuted(uuid)) return;

        mongoUtil.getMuted().deleteOne(new Document("uuid", uuid.toString()));
    }

    public boolean isMuted(UUID uuid){
        return mongoUtil.getMuted().countDocuments(new Document("uuid", uuid.toString())) > 0;
    }

    public void checkDuration(UUID uuid){
        if(!isMuted(uuid)) return;

        if(getEnd(uuid) == -1) return;

        if(getEnd(uuid) < System.currentTimeMillis()){
            unmute(uuid);
        }
    }

    public long getEnd(UUID uuid){
        if(!isMuted(uuid)) return 0;

       return getDocument(uuid).getLong("end");
    }

    public String getTimeLeft(UUID uuid){
        if(!isMuted(uuid)) return "§cNon muet";

        if(getEnd(uuid) == -1){
            return "§cPermanent";
        }

        long tempsRestant = (getEnd(uuid) - System.currentTimeMillis()) / 1000;
        int annee = 0;
        int mois = 0;
        int jours = 0;
        int heures = 0;
        int minutes = 0;
        int secondes = 0;

        while(tempsRestant >= TimeUnit.ANNEE.getToSecond()){
            annee++;
            tempsRestant -= TimeUnit.ANNEE.getToSecond();
        }

        while(tempsRestant >= TimeUnit.MOIS.getToSecond()){
            mois++;
            tempsRestant -= TimeUnit.MOIS.getToSecond();
        }

        while(tempsRestant >= TimeUnit.JOUR.getToSecond()){
            jours++;
            tempsRestant -= TimeUnit.JOUR.getToSecond();
        }

        while(tempsRestant >= TimeUnit.HEURE.getToSecond()){
            heures++;
            tempsRestant -= TimeUnit.HEURE.getToSecond();
        }

        while(tempsRestant >= TimeUnit.MINUTE.getToSecond()){
            minutes++;
            tempsRestant -= TimeUnit.MINUTE.getToSecond();
        }

        while(tempsRestant >= TimeUnit.SECONDE.getToSecond()){
            secondes++;
            tempsRestant -= TimeUnit.SECONDE.getToSecond();
        }

        // 1 An, 1 Mois, 1 Jour(s), 12 Heure(s), 32 Minute(s), 12 Seconde(s)
        return (annee == 0 ? "" : annee + " " + TimeUnit.ANNEE.getName() + ", ")
                + (mois == 0 ? "" : mois + " " + TimeUnit.MOIS.getName() + ", ")
                + (jours == 0 ? "" : jours + " " + TimeUnit.JOUR.getName() + ", ")
                + (heures == 0 ? "" : heures + " " + TimeUnit.HEURE.getName() + ", ")
                + (minutes == 0 ? "" : minutes + " " + TimeUnit.MINUTE.getName() + ", ")
                + (secondes == 0 ? "" : secondes + " " + TimeUnit.SECONDE.getName());
    }

    public String getReason(UUID uuid){
        if(!isMuted(uuid)) return "§cNon muet";

        return getDocument(uuid).getString("reason");
    }

    public String getAuthor(UUID uuid){
        if(!isMuted(uuid)) return "§cNon muet";

        return getDocument(uuid).getString("author");
    }

    private Document getDocument(UUID uuid){
        return mongoUtil.getMuted().find(new Document("uuid", uuid.toString())).limit(1).cursor().next();
    }
}