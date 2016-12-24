package org.graviton.network.game.protocol;

import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.flag.FlagAttribute;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;
import java.util.TimeZone;

/**
 * Created by Botan on 11/12/2016. 02:37
 */
public class FightPacketFormatter {

    public static String newFightMessage(FightState fightState, boolean canCancel, boolean isChallenge, boolean isSpectator, int remainingTime, FightType fightType) {
        return "GJK" + fightState.ordinal() + "|" +
                (canCancel ? "1" : "0") + "|" +
                (isChallenge ? "1" : "0") + "|" +
                (isSpectator ? "1" : "0") + "|" +
                remainingTime + "|" +
                fightType.ordinal();
    }

    public static String startCellsMessage(String places, FightSide team) {
        return "GP" + places + "|" + team.ordinal();
    }

    public static String showFighter(Fighter fighter) {
        StringBuilder builder = new StringBuilder();
        builder.append("GM|+");
        builder.append(fighter.getFightCell().getId()).append(';');
        builder.append("1;0;");//1; = Orientation
        builder.append(fighter.getId()).append(';');
        builder.append(fighter.getName()).append(';');
        builder.append(fighter.getFightGM());
        return builder.toString();
    }

    public static String showFighters(Collection<Fighter> fighters) {
        StringBuilder builder = new StringBuilder();
        fighters.forEach(fighter -> builder.append(showFighter(fighter)));
        return builder.toString();
    }

    public static String addFlagMessage(int fightId, FightType fightType, Fighter challenger, Fighter defender) {
        StringBuilder builder = new StringBuilder().append("Gc+");
        builder.append(fightId).append(';').append(fightType.ordinal()).append('|');

        builder.append(challenger.getId()).append(';').append(challenger.getLastLocation().getCell().getId()).append(';')
                .append("0;")                 // {player: 0, monster: 1}
                .append("-1").append('|');    // alignment

        builder.append(defender.getId()).append(';').append(defender.getLastLocation().getCell().getId()).append(';').append("0;").append("-1");
        return builder.toString();
    }

    public static String teamMessage(int leaderId, Collection<Fighter> fighters) {
        StringBuilder builder = new StringBuilder("Gt").append(leaderId);

        fighters.forEach(fighter -> {
            builder.append("|+");
            builder.append(fighter.getId()).append(';');
            builder.append(fighter.getName()).append(';');
            builder.append(fighter.getLevel());
        });

        return builder.toString();
    }

    public static String removeFlagMessage(int fightId) {
        return "Gc-" + fightId;
    }

    public static String flagAttributeMessage(boolean active, FlagAttribute attribute, int leaderId) {
        return "Go" + (active ? "+" : "-") + (attribute == FlagAttribute.DENY_ALL ? "A" : attribute.toString()) + leaderId;
    }

    public static String fighterLeft() {
        return "GV";
    }


    public static String fightersPlacementMessage(Collection<Fighter> fighters) {
        StringBuilder builder = new StringBuilder("GIC");

        fighters.forEach(fighter -> {
            builder.append('|');
            builder.append(fighter.getId()).append(';');
            builder.append(fighter.getFightCell().getId()).append(";1");
        });
        return builder.toString();
    }

    public static String fighterPlacementMessage(int fighterId, short newCellId) {
        return "GIC|" + fighterId + ";" + newCellId + ";1";
    }

    public static String cannotJoinMessage(int id, char error) {
        return "GA;903;" + id + ';' + String.valueOf(error);
    }

    public static String showCellMessage(int id, short cell) {
        return ("Gf" + id + '|') + cell;
    }

    public static String fighterReadyMessage(long fighterId, boolean ready) {
        return "GR" + (ready ? "1" : "0") + fighterId;
    }

    public static String fightStartMessage() {
        return "GS";
    }

    public static String turnListMessage(Collection<FightTurn> turns) {
        StringBuilder builder = new StringBuilder().append("GTL");
        turns.forEach(turn -> builder.append('|').append(turn.getFighter().getId()));
        return builder.toString();
    }

    public static String fighterInformationMessage(Collection<Fighter> fighters) {
        StringBuilder builder = new StringBuilder("GTM");

        fighters.forEach(fighter -> {
            builder.append('|');

            builder.append(fighter.getId()).append(';');
            builder.append(fighter.isDead() ? '1' : '0').append(';');

            if (!fighter.isDead()) {
                builder.append(fighter.getStatistics().getCurrentLife()).append(';');
                builder.append(fighter.getStatistics().get(CharacteristicType.ActionPoints).total()).append(';');
                builder.append(fighter.getStatistics().get(CharacteristicType.MovementPoints).total()).append(';');
                builder.append(fighter.getFightCell().getId()).append(';');
                builder.append(';'); //todo ???
                builder.append(fighter.getStatistics().getMaxLife());
            }
        });

        return builder.toString();
    }

    public static String turnStartMessage(int fighterId, long remainingTime) {
        return "GTS" + fighterId + "|" + remainingTime;
    }

    public static String turnEndMessage(int fighterId) {
        return "GTF" + fighterId;
    }

    public static String turnReadyMessage(int fighterId) {
        return "GTR" + fighterId;
    }

    public static String fightEndMessage(long fightDuration, Fighter leaderWinner, Collection<Fighter> winners, Collection<Fighter> losers) {
        StringBuilder builder = new StringBuilder("GE");
        //GE4166|958|0|2;1424;Leo-mars;181;0;1317997000;1332575192;1355584000         ; ; ; ;; |0;958;Abcdefghijkl;1  ;0;0         ;0         ;110                ; ; ; ; |
        builder.append(fightDuration).append('|');
        builder.append(leaderWinner.getId()).append('|');
        builder.append('0').append('|');

        formatFightNormalEndMessage(builder, winners, true);
        formatFightNormalEndMessage(builder, losers, false);

        return builder.toString();
    }

    private static void formatFightNormalEndMessage(StringBuilder builder, Collection<Fighter> team, boolean winner) {
        for (Fighter fighter : team) {
            formatFightNormalEndMessage(builder, fighter, winner);
            builder.append('|');
        }
    }

    private static void formatFightNormalEndMessage(StringBuilder builder, Fighter fighter, boolean winner) {
        builder.append(winner ? '2' : '0').append(';');
        builder.append(fighter.getId()).append(';');
        builder.append(fighter.getName()).append(';');
        builder.append(fighter.getLevel()).append(';');
        builder.append(winner ? fighter.isDead() ? '1' : '0' : '1').append(';');

        builder.append(0).append(';'); //min exp
        builder.append(1).append(';'); //current xp
        builder.append(2).append(';'); //max XP

        builder.append("0").append(';'); //Xp gagné
        builder.append("0").append(';'); //guilde xp gagné
        builder.append("0").append(';'); //mount xp gagné
        builder.append(';');//todo won items
        builder.append("0"); //kamas gagné
    }

    public static String fighterDieMessage(int fighter) {
        return "GA;103;" + fighter + ';' + fighter;
    }

    public static String duelFightMessage(Fight fight) {
        String builder = fight.getId() + ";";
        builder += fight.getStartTime().getTime() + TimeZone.getDefault().getRawOffset() + ";";
        builder += "0,0," + fight.getFirstTeam().getFighters().size() + ";" + "0,0," + fight.getSecondTeam().getFighters().size() + ";|";
        return builder;
    }

    public static String endFightActionMessage(short fightAction, int fighterId) {
        return "GAF" + fightAction + "|" + fighterId;
    }

    public static String startActionMessage(int fighterId) {
        return "GAS" + fighterId;
    }

    public static String looseMovementPointMessage(int fighterId, byte movementPointUsed) {
        return actionMessage(FightAction.LOOSE_MOVEMENT_POINT, fighterId, String.valueOf(fighterId), String.valueOf(movementPointUsed));
    }

    public static String looseActionPointMessage(int fighterId, byte actionPointUsed) {
        return actionMessage(FightAction.LOOSE_ACTION_POINT, fighterId, String.valueOf(fighterId), String.valueOf(actionPointUsed));
    }

    public static String actionMessage(FightAction actionType, int fighterId, String... extra) {
        StringBuilder builder = new StringBuilder("GA;");

        builder.append(actionType.getId()).append(';');
        builder.append(fighterId).append(';');

        if (extra != null) {
            for (String arg : extra) {
                builder.append(arg);
                builder.append(',');
            }
        }

        return builder.substring(0, builder.length() - 1);
    }


}
