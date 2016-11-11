package org.graviton.game.statistics;


import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 11/11/2016 : 21:12
 */
public class PlayerStatistics {
    private PlayerLife life;
    private PlayerPods pods;

    private short statisticPoints, spellPoints, energy;

    public PlayerStatistics(Record record) {
        this.statisticPoints = record.get(PLAYERS.STAT_POINTS);
        this.spellPoints = record.get(PLAYERS.SPELL_POINTS);
        this.energy = record.get(PLAYERS.ENERGY);
    }

    public PlayerStatistics() {
        this.statisticPoints = 0;
        this.spellPoints = 0;
        this.energy = 10000;
    }


}
