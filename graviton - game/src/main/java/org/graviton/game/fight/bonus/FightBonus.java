package org.graviton.game.fight.bonus;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.team.MonsterTeam;
import org.graviton.game.items.Item;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.utils.Utils;

import java.util.List;

/**
 * Created by Botan on 02/02/2017. 14:57
 */

@Data
public class FightBonus {
    private static double[] bonusCoefficient = {0.5, 1.1, 1.5, 2.3, 3.1, 3.6, 4.2, 4.7};

    private final Player fighter;
    private final Fight fight;

    private long experience;
    private long kamas;
    private List<Item> items;

    public FightBonus(Fight fight, Player fighter, DropBonus dropBonus) {
        this.fight = fight;
        this.fighter = fighter;
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

        this.experience = (long) ((double) ((1 + ((wisdom + (monsterGroup.getStars() * 20)) / 100)) *
                (bonus + groupCoefficient) * (monsterGroup.getBaseExperience() / monsterGroup.getMonsters().size())));
        this.kamas = monsterGroup.randomKamas();
        this.items = dropBonus.getItemsByPlayer(fighter.getId());
    }

    public void validate() {
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
