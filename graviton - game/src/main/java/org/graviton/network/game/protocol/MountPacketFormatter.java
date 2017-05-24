package org.graviton.network.game.protocol;

import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.guild.Guild;
import org.graviton.game.mountpark.MountPark;

/**
 * Created by Botan on 25/12/2016. 02:13
 */
public class MountPacketFormatter {

   public static String showMountParkMessage(MountPark park, EntityFactory entityFactory) {
        StringBuilder builder = new StringBuilder("Rp");

       builder.append(park.getOwner()).append(";").append(park.getPrice()).append(";").
               append(park.getSize()).append(";").append(park.getObjectSize()).append(";");

       if(park.getGuild() > 0) {
           Guild guild = entityFactory.getGuildRepository().find(park.getGuild());
           builder.append(guild.getName()).append(";").append(guild.getEmblem());
       } else builder.append(";");

        return builder.toString();
   }

   public static String buildPersonalParkMessage(MountPark mountPark, Player player) {
       StringBuilder builder = new StringBuilder("ECK|");
       return builder.toString();
   }

}
