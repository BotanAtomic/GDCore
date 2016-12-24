package org.graviton.game.statistics;

import org.graviton.game.breeds.models.Sacrieur;
import org.graviton.game.client.player.Player;
import org.graviton.game.statistics.common.Characteristic;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.game.statistics.type.PlayerStatistics;

/**
 * Created by Botan on 22/12/2016. 00:17
 */
public class Initiative extends Characteristic {
    private final PlayerStatistics playerStatistics;

    public Initiative(PlayerStatistics playerStatistics, short base) {
        super(base);
        this.playerStatistics = playerStatistics;
    }

    private Player player() {
        return playerStatistics.getPlayer();
    }

    private short getMaxLife() {
        return playerStatistics.getMaxLife();
    }

    private short getCurrentLife() {
        return playerStatistics.getCurrentLife();
    }

    private Characteristic get(CharacteristicType type) {
        return playerStatistics.get(type);
    }

    @Override
    public short total() {
        double total = ((getMaxLife() - 55) / (byte) (player().getBreed() instanceof Sacrieur ? 8 : 4)) + (short) (get(CharacteristicType.Strength).total() +
                get(CharacteristicType.Intelligence).total() + get(CharacteristicType.Chance).total() + get(CharacteristicType.Agility).total());
        return (short) (total * ((double) getCurrentLife() / (double) getMaxLife()));
    }
}


