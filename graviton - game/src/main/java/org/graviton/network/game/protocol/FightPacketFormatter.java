package org.graviton.network.game.protocol;

import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.alignment.type.AlignmentType;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.monster.MonsterGroup;
import org.graviton.game.experience.Experience;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.bonus.FightBonus;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.fight.common.FightSide;
import org.graviton.game.fight.common.FightState;
import org.graviton.game.fight.common.FightType;
import org.graviton.game.fight.flag.FlagAttribute;
import org.graviton.game.fight.team.FightTeam;
import org.graviton.game.fight.team.PlayerTeam;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.items.Item;
import org.graviton.game.spell.common.SpellEffects;
import org.graviton.game.statistics.common.CharacteristicType;

import java.util.Collection;
import java.util.List;
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
                remainingTime * 1000 + "|" +
                fightType.ordinal();
    }

    public static String startCellsMessage(String places, FightSide team) {
        return "GP" + places + "|" + team.ordinal();
    }

    public static String showFighter(Fighter fighter) {
        return "GM|+" + fighter.getFightCell().getId() + ';' + "1;0;" + fighter.getId() + ';' + fighter.getName() + ';' + fighter.getFightGM();
    }

    public static String showFighters(Collection<Fighter> fighters) {
        StringBuilder builder = new StringBuilder();
        fighters.forEach(fighter -> builder.append(showFighter(fighter)));
        return builder.toString();
    }

    public static String addFlagMessage(int fightId, FightType fightType, Fighter challenger, Fighter defender) {
        return "Gc+" + fightId + ';' + fightType.ordinal() + '|' + challenger.getId() + ';' + challenger.getLastLocation().getCell().getId() + ';' +
                "0;" +
                "-1" +  // alignment
                '|' + defender.getId() + ';' + defender.getLastLocation().getCell().getId() + ';' + "0;" + "-1";
    }

    public static String addAggressionFlagMessage(int fightId, FightType fightType, Fighter challenger, Fighter defender) {
        return "Gc+" + fightId + ';' + fightType.ordinal() + '|' + challenger.getId() + ';' + challenger.getLastLocation().getCell().getId() + ';' +
                "0;" +
                challenger.getAlignment().getId() +  // alignment
                '|' + defender.getId() + ';' + defender.getLastLocation().getCell().getId() + ';' + "0;" + defender.getAlignment().getId();
    }

    public static String addMonsterFlagMessage(int fightId, FightType fightType, Fighter challenger, MonsterGroup defender) {
        return "Gc+" + fightId + ';' + fightType.ordinal() + '|' + challenger.getId() + ';' + challenger.getLastLocation().getCell().getId() + ';' +
                "0;" +
                "-1" +  // alignment
                '|' + defender.getId() + ';' +
                defender.getLocation().getCell().getId() + ';' + "1;" + "-1";
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

            short actionPoint = (short) fighter.getStatistics().get(CharacteristicType.ActionPoints).total();
            short movementPoint = (short) fighter.getStatistics().get(CharacteristicType.MovementPoints).total();

            if (!fighter.isDead()) {
                builder.append(fighter.getStatistics().getLife().getCurrent()).append(';');
                builder.append(actionPoint < 0 ? 0 : actionPoint).append(';');
                builder.append(movementPoint < 0 ? 0 : movementPoint).append(';');
                builder.append(fighter.getFightCell().getId()).append(';');
                builder.append(';'); //todo ???
                builder.append(fighter.getStatistics().getLife().getMaximum());
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


    private static StringBuilder startEndFightMessage(long fightDuration, int winnerId, boolean aggresion) {
        StringBuilder builder = new StringBuilder("GE");
        builder.append(fightDuration).append('|');
        builder.append(winnerId).append('|');
        builder.append(aggresion ? 1 : 0).append('|');
        return builder;
    }

    public static String fightEndMessage(long fightDuration, int winnerId, FightTeam winners, FightTeam losers) {
        StringBuilder builder = startEndFightMessage(fightDuration, winnerId, false);

        formatFightNormalEndMessage(builder, winners, true);
        formatFightNormalEndMessage(builder, losers, false);

        return builder.toString();
    }

    public static String aggressionFightEndMessage(long fightDuration, int winnerId, FightTeam winners, FightTeam losers) {
        StringBuilder builder = startEndFightMessage(fightDuration, winnerId, true);

        boolean winnerDishonor = losers.stream().filter(looser -> looser.getAlignment().getType() == AlignmentType.NEUTRE).count() > 0;
        boolean looserDishonor = winners.stream().filter(winner -> winner.getAlignment().getType() == AlignmentType.NEUTRE).count() > 0;

        winners.stream().filter(fighter -> fighter instanceof Player).forEach(winner -> {
            if(winnerDishonor)
                winner.getFightBonus().setDishonor((byte) 1);

            formatAggressionFightPlayerEndMessage(builder, winner, true);
        });

        losers.stream().filter(fighter -> fighter instanceof Player).forEach(looser -> {
            if(looserDishonor) {
                looser.setFightBonus(FightBonus.create());
                looser.getFightBonus().setDishonor((byte) 1);
            }

            formatAggressionFightPlayerEndMessage(builder, looser, false);
        });

        return builder.toString();
    }

    private static void formatFightNormalEndMessage(StringBuilder builder, FightTeam team, boolean winner) {
        team.forEach(fighter -> {
            if (!fighter.isInvocation()) {
                if (team instanceof PlayerTeam)
                    formatFightPlayerEndMessage(builder, fighter, winner);
                else
                    formatFightMonsterEndMessage(builder, fighter, winner);
                builder.append('|');
            }
        });
    }

    //todo : revoir tt cette merde
    private static void formatFightPlayerEndMessage(StringBuilder builder, Fighter fighter, boolean winner) {
        builder.append(winner ? '2' : '0').append(';');
        builder.append(fighter.getId()).append(';');
        builder.append(fighter.getName()).append(';');
        builder.append(fighter.getLevel()).append(';');
        builder.append(winner ? fighter.isDead() ? '1' : '0' : '1').append(';');

        Player player = (Player) fighter.getCreature();

        Experience experience = player.getEntityFactory().getExperience(player.getLevel());
        builder.append(experience.getPlayer()).append(';');
        builder.append(player.getExperience()).append(';');
        builder.append(experience.getNext() == null ? experience.getPlayer() : experience.getNext().getPlayer()).append(';');


        builder.append(fighter.getFightBonus() == null ? 0 : fighter.getFightBonus().getExperience()).append(';'); //wp won
        builder.append(fighter.getFightBonus() == null ? 0 : fighter.getFightBonus().getGuildExperience()).append(';'); //guild xp won
        builder.append('0').append(';'); //mount xp won
        builder.append(fighter.getFightBonus() == null ? ';' : formatItems(fighter.getFightBonus().getItems())); // items won
        builder.append(fighter.getFightBonus() == null ? 0 : fighter.getFightBonus().getKamas()); //kamas won
    }

    private static void formatAggressionFightPlayerEndMessage(StringBuilder builder, Fighter fighter, boolean winner) {
        builder.append(winner ? '2' : '0').append(';');
        builder.append(fighter.getId()).append(';');
        builder.append(fighter.getName()).append(';');
        builder.append(fighter.getLevel()).append(';');
        builder.append(winner ? fighter.isDead() ? '1' : '0' : '1').append(';');

        Player player = (Player) fighter.getCreature();
        Alignment alignment = player.getAlignment();
        EntityFactory entityFactory = player.getEntityFactory();

        builder.append(alignment.getType() == AlignmentType.NEUTRE ? 0 : entityFactory.getExperience(alignment.getGrade()).getAlignment()).append(";");
        builder.append(alignment.getHonor()).append(";");
        builder.append(alignment.getType() == AlignmentType.NEUTRE ? 0 : entityFactory.getExperience(alignment.getGrade()).next().getAlignment()).append(";");
        builder.append(player.getFightBonus() == null ? 0 : player.getFightBonus().getHonor()).append(";");
        builder.append(alignment.getGrade()).append(";");
        builder.append(alignment.getDishonor()).append(";");
        builder.append(player.getFightBonus() == null ? 0 : player.getFightBonus().getDishonor());
        builder.append(";");
        builder.append(";0;0;0;0;0|");
    }

    private static String formatItems(List<Item> items) {
        StringBuilder builder = new StringBuilder();
        items.forEach(item -> builder.append(item.getTemplate().getId()).append('~').append(item.getQuantity()).append(','));
        return builder.substring(0, builder.length() > 0 ? builder.length() - 1 : 0) + ";";
    }

    private static void formatFightMonsterEndMessage(StringBuilder builder, Fighter fighter, boolean winner) {
        builder.append(winner ? '2' : '0').append(';');
        builder.append(fighter.getId()).append(';');
        builder.append(fighter.getName()).append(';');
        builder.append(fighter.getLevel()).append(';');
        builder.append(winner ? fighter.isDead() ? '1' : '0' : '1').append(';');
        builder.append("0;0;0;0;0;0;;0");
    }

    public static String fighterDieMessage(int fighter) {
        return "GA;103;" + fighter + ';' + fighter;
    }

    public static String fightMessage(Fight fight) {
        String builder = fight.getId() + ";";
        builder += fight.getStartTime().getTime() + TimeZone.getDefault().getRawOffset() + ";";
        builder += "0,0," + fight.getFirstTeam().size() + ";" + "0,0," + fight.getSecondTeam().size() + ";|";
        return builder;
    }

    public static String endFightActionMessage(short fightAction, int fighterId) {
        return "GAF" + fightAction + "|" + fighterId;
    }

    public static String startActionMessage(int fighterId) {
        return "GAS" + fighterId;
    }

    public static String movementPointEventMessage(int fighterId, byte movementPointUsed) {
        return actionMessage(FightAction.MOVEMENT_POINT_EVENT, fighterId, String.valueOf(fighterId), String.valueOf(movementPointUsed));
    }

    public static String actionPointEventMessage(int fighterId, byte actionPointUsed) {
        return actionMessage(FightAction.ACTION_POINT_EVENT, fighterId, String.valueOf(fighterId), String.valueOf(actionPointUsed));
    }

    public static String lifeEventMessage(int fighterId, int targetId, int life) {
        return actionMessage(FightAction.LIFE_EVENT, fighterId, String.valueOf(targetId), String.valueOf(life));
    }

    public static String actionMessage(FightAction actionType, int fighterId, Object... extra) {
        return actionMessage(actionType.getId(), fighterId, extra);
    }

    public static String actionMessage(short actionType, int fighterId, Object... extra) {
        StringBuilder builder = new StringBuilder("GA;");

        builder.append(actionType).append(';');
        builder.append(fighterId).append(';');

        if (extra != null) {
            for (Object arg : extra) {
                builder.append(String.valueOf(arg));
                builder.append(',');
            }
        }

        return builder.substring(0, builder.length() - 1);
    }

    public static String fighterBuffMessage(int fighterId, SpellEffects effectId, int value1, int value2, int value3, int chance, short remainingTurn, int spellId) {
        return "GIE" + effectId.value() + ";" +
                fighterId + ";" +
                (value1 == 0 ? "" : value1) + ";" +
                (value2 == 0 ? "" : value2) + ";" +
                (value3 == 0 ? "" : value3) + ";" +
                (chance == 0 ? "" : chance) + ";" +
                remainingTurn + ";" +
                spellId;
    }

    public static String trapUsedMessage(long triggerId, int originalSpellId, short triggerCellId, long trapOwnerId) {
        return "GA1;306;" + triggerId + ";" + originalSpellId + "," + triggerCellId + ",407,1,1," + trapOwnerId;
    }

    public static String trapDeletedMessage(long trapOwnerId, short trapCellId, int trapSize, int color) {
        return "GA;999;" + trapOwnerId + ";GDZ-" + trapCellId + ";" + trapSize + ";" + color;
    }

    public static String localTrapDeleteMessage(long trapOwnerId, short trapCellId) {
        return "GA;999;" + trapOwnerId + ";GDC" + trapCellId;
    }

    public static String trapAddedMessage(long trapOwnerId, short trapCellid, int trapSize, int color) {
        return "GA;999;" + trapOwnerId + ";GDZ+" + trapCellid + ";" + trapSize + ";" + color;
    }

    public static String localTrapAddedMessage(long trapOwnerId, short trapCellId) {
        return "GA;999;" + trapOwnerId + ";GDC" + trapCellId + ";Haaaaaaaaz3005;";
    }

    public static String cannotJoinFightAsSpectator() {
        return MessageFormatter.customMessage("157");
    }

    public static String joinFightAsSpectator(String name) {
        return MessageFormatter.customMessage("036;" + name);
    }

    public static String disconnectedPlayerMessage(String name, byte remainingTurn) {
        return MessageFormatter.customMessage("1182;" + name + "~" + remainingTurn);
    }

    public static String reconnectedPlayerMessage(String name) {
        return MessageFormatter.customMessage("1184;" + name);
    }
}
