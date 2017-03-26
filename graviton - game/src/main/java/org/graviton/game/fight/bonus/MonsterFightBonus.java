package org.graviton.game.fight.bonus;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.team.MonsterTeam;
import org.graviton.game.guild.GuildMember;
import org.graviton.game.items.Item;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.utils.Utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Botan on 02/02/2017. 14:57
 */

@Data
public class MonsterFightBonus extends FightBonus{
    private static double[] bonusCoefficient = {0.5, 1.1, 1.5, 2.3, 3.1, 3.6, 4.2, 4.7};


    public MonsterFightBonus(Fight fight, Player fighter, DropBonus dropBonus) {
        super(fighter, fight);
        calculate(dropBonus);
    }

    private void calculate(DropBonus dropBonus) {
        MonsterGroup monsterGroup = ((MonsterTeam) fight.getSecondTeam()).getMonsterGroup();

        int winnerLevel = fighter.getTeam().getLevel();
        int looserLevel = fight.otherTeam(fighter.getTeam()).getLevel();
        double maximumMonsterLevel = monsterGroup.getMaximumLevel();

        double bonus = bonusCoefficient[(int) fighter.getTeam().stream().filter(fighter -> fighter.getLevel() > (maximumMonsterLevel / 3)).count()];
        double groupCoefficient = Utils.range(winnerLevel, looserLevel * 0.90, looserLevel * 1.10) ? 1 : Utils.difference(looserLevel, winnerLevel) * 0.002;
        double wisdom = 1 + ((double) fighter.getStatistics().get(CharacteristicType.Wisdom).total() / (double) 100);

        System.err.println("Winner level = " + winnerLevel);
        System.err.println("looserLevel = " + looserLevel);
        System.err.println("maximumMonsterLevel = " + maximumMonsterLevel);
        System.err.println("bonus = " + bonus);
        System.err.println("wisdom = " + wisdom);
        System.err.println("monsterGroup.getStars() = " + monsterGroup.getStars());
        System.err.println("monsterGroup.getBaseExperience() / monsterGroup.getMonsters().size()) = " + monsterGroup.getBaseExperience() / monsterGroup.getMonsters().size());
        System.err.println("getBaseExperience = " + monsterGroup.getBaseExperience());

        AtomicLong experience = new AtomicLong((long) ((double) ((1 + ((wisdom + (monsterGroup.getStars() * 20)) / 100)) *
                (bonus + groupCoefficient) * (monsterGroup.getBaseExperience() / monsterGroup.getMonsters().size()))));

        if (fighter.getGuild() != null) {
            GuildMember guildMember = fighter.getGuild().getMember(fighter.getId());
            if (guildMember != null) {
                this.guildExperience = getGuildExperience(fighter, guildMember, experience);
                guildMember.addExperience(guildExperience);
                fighter.getGuild().addExperience(guildExperience, fighter.getEntityFactory());
            }
        }

        this.experience = experience.get();
        this.kamas = monsterGroup.randomKamas();
        this.items = dropBonus.getItemsByPlayer(fighter.getId());

        validate();
    }

    private long getGuildExperience(Player player, GuildMember guildMember, AtomicLong atomicExperience) {
        double experience = (double) atomicExperience.get(),
                level = player.getLevel(),
                guildLevel = player.getGuild().getLevel(),
                givenPercent = (double) guildMember.getGivenPercent() / 100;

        double percentMax = experience * givenPercent * 0.10;
        double difference = Math.abs(level - guildLevel);

        double toGuild = difference >= 70 ? percentMax * 0.10 : ((difference >= 31 && difference <= 69) ?
                percentMax - ((percentMax * 0.10) * (Math.floor((difference + 30) / 10))) : (difference >= 10 && difference <= 30) ?
                percentMax - ((percentMax * 0.20) * (Math.floor(difference / 10))) : percentMax);

        atomicExperience.set((long) (experience - (experience * givenPercent)));
        return Math.round(toGuild);
    }

    private void validate() {
        this.items.forEach(item -> {
            fighter.getInventory().addItem(item, true);
            fighter.send(ItemPacketFormatter.addItemMessage(item));
        });
        fighter.getInventory().addKamas(this.kamas);
        fighter.addExperience(this.experience);
        fighter.send(PlayerPacketFormatter.asMessage(fighter, fighter.getEntityFactory().getExperience(fighter.getLevel()), fighter.getAlignment(), fighter.getStatistics()));
        fighter.update();
    }

}
