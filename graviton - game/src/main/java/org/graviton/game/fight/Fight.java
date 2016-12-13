package org.graviton.game.fight;

import lombok.Data;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.protocol.FightPacketFormatter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Botan on 10/12/2016. 21:44
 */

@Data
public abstract class Fight {
    private final Date startTime = new Date();

    private final int id;
    private final GameMap oldMap, actualMap;
    private final FightTeam firstTeam, secondTeam;

    protected Fight(int id, FightTeam firstTeam, FightTeam secondTeam, GameMap gameMap) {
        this.id = id;
        this.oldMap = gameMap;
        this.actualMap = gameMap.copy();

        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;

        startInitialization();
    }

    private void startInitialization() {
        List<FightTeam> teams = Arrays.asList(firstTeam, secondTeam);

        teams.forEach(fightTeam -> fightTeam.outMap(oldMap));
        send(FightPacketFormatter.newFightMessage(canQuit(), scheduledTime() * 1000, getType()), teams);

        teams.forEach(FightTeam::placeFighters);

        sendPlaces();

        send(FightPacketFormatter.showFighters(fighters()), teams);
    }

    private Collection<Fighter> fighters() {
        return Stream.concat(firstTeam.getFighters().stream(), secondTeam.getFighters().stream()).filter(new ArrayList<>()::add).collect(Collectors.toList());
    }

    private void sendPlaces() {
        send(FightPacketFormatter.startCellsMessage(firstTeam.startCellsMessage(), secondTeam.startCellsMessage(), firstTeam.getSide()), firstTeam);
        send(FightPacketFormatter.startCellsMessage(secondTeam.startCellsMessage(), firstTeam.startCellsMessage(), secondTeam.getSide()), secondTeam);
    }

    private void send(String data, Collection<FightTeam> teams) {
        teams.forEach(fightTeam -> fightTeam.send(data));
    }

    private void send(String data, FightTeam team) {
        team.send(data);
    }

    protected abstract byte scheduledTime();

    protected abstract byte canQuit();

    protected abstract FightType getType();
}
