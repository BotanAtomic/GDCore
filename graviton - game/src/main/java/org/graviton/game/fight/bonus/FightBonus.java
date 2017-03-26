package org.graviton.game.fight.bonus;

import lombok.Data;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.items.Item;

import java.util.List;

/**
 * Created by Botan on 12/03/2017. 20:26
 */

@Data
public class FightBonus {
    protected final Player fighter;
    protected final Fight fight;

    protected long experience;
    protected short honor;
    protected byte dishonor;
    protected long guildExperience;
    protected long kamas;
    protected List<Item> items;

    public static FightBonus create() {
        return new FightBonus(null,null);
    }

    FightBonus(Player fighter, Fight fight) {
        this.fighter = fighter;
        this.fight = fight;
    }
}
