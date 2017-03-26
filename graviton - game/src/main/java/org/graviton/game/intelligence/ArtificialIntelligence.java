package org.graviton.game.intelligence;

import javafx.util.Pair;
import org.graviton.collection.CollectionQuery;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.interaction.actions.FightMovement;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.paths.MonsterPath;
import org.graviton.game.spell.Spell;
import org.graviton.game.spell.SpellEffect;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Botan on 18/01/2017. 23:30
 */
public abstract class ArtificialIntelligence {
    protected final Fighter fighter;
    protected final FightTurn fightTurn;
    protected byte count;

    protected ArtificialIntelligence(Fighter fighter) {
        this.fighter = fighter;
        this.fightTurn = fighter.getTurn();

    }

    public abstract void run();

    protected static Fighter getNearestEnemy(Fight fight, Fighter fighter) {
        AtomicInteger distanceRequired = new AtomicInteger(1000);
        AtomicReference<Fighter> current = new AtomicReference<>();
        fight.fighters().stream().filter(target -> target.getTeam().getSide().ordinal() != fighter.getTeam().getSide().ordinal()).filter(Fighter::isAlive).forEach(target -> {
            int distance = Cells.distanceBetween(fight.getFightMap().getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId());
            if (distance < distanceRequired.get()) {
                distanceRequired.set(distance);
                current.set(target);
            }
        });
        return current.get();
    }

    protected static Fighter getNearestEnemy(Fight fight, Fighter fighter, byte minimumRange, byte maximumRange) {
        AtomicInteger distanceRequired = new AtomicInteger(maximumRange);
        AtomicReference<Fighter> current = new AtomicReference<>();
        fight.fighters().stream().filter(target -> target.getTeam().getId() != fighter.getTeam().getId()).filter(Fighter::isAlive).forEach(target -> {
            int distance = Cells.distanceBetween(fight.getFightMap().getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId());
            if (distance < distanceRequired.get() && distance > minimumRange) {
                distanceRequired.set(distance);
                current.set(target);
            }
        });
        return current.get();
    }

    protected static short tryToMove(Fight fight, Fighter fighter, Fighter initialTarget, boolean diagonal) {
        if (initialTarget == null)
            return 0;

        FightMap map = fight.getFightMap();
        Cell cell = fighter.getFightCell();
        Cell targetCell = initialTarget.getFightCell();

        if (Cells.isNextTo(cell.getId(), targetCell.getId()))
            return 0;

        AtomicReference<Short> foundCell = new AtomicReference<>(diagonal ? Cells.getNearestCellDiagonal(map, cell.getId(), targetCell.getId()) : Cells.getNearestCellAround(map, targetCell.getId(), cell.getId()));

        if (foundCell.get() == -1) {
            CollectionQuery.from(fight.fighters()).filter(target -> target.getTeam().getId() != fighter.getTeam().getId()).orderBy(Fighter.compareByLife()).forEach(target -> {
                short newCell = diagonal ? Cells.getNearestCellDiagonal(map, cell.getId(), target.getFightCell().getId()) : Cells.getNearestCellAround(map,target.getFightCell().getId(), cell.getId());

                if (newCell != -1 && foundCell.get() == -1)
                    foundCell.set(newCell);
            });
        }

        System.err.println("Found cell = " + foundCell.get());
        return move(fighter, foundCell.get());
    }

    protected static short move(Fighter fighter, short destination) {
        Cell cell = fighter.getFightCell();
        List<Cell> path = new MonsterPath(fighter.getFight().getFightMap(), cell.getId(), destination).getShortestPath();

        if (path == null || path.isEmpty())
            return 0;


        List<Cell> finalPath = new LinkedList<>();

        for (int a = 0; a < fighter.getCurrentMovementPoint(); a++) {
            if (path.size() == a)
                break;
            finalPath.add(path.get(a));
        }

        Pair<String, Byte> compiledPath = MonsterPath.compilePath(finalPath, cell.getId(), fighter.getFight().getFightMap());

        AbstractGameAction fightMovement = new FightMovement(fighter, compiledPath.getKey());

        if (fightMovement.begin())
            fightMovement.finish(null);
        else return 0;

        return (short) (compiledPath.getValue() * (finalPath.size() >= 4 ? 400 : 600));
    }

    protected static boolean tryToInvoke(Fight fight, Fighter fighter, List<Spell> invocations) {
        if (fighter.getInvocations().size() >= fighter.getStatistics().get(CharacteristicType.Summons).total())
            return false;

        Fighter firstTarget = getNearestEnemy(fight, fighter);

        if (firstTarget == null)
            return false;


        Spell spell = getBestInvocationSpell(fighter, invocations);

        if (spell == null)
            return false;

        short firstCell = Cells.getCellBetweenEnemy(fighter.getFightCell().getId(), fight.getFightMap());


        if (firstCell == -1)
            return false;


        spell.applyToFight(fighter, fight.getFightMap().getCells().get(firstCell));
        return true;
    }

    private static Spell getBestInvocationSpell(Fighter fighter, List<Spell> invocations) {
        Optional<Spell> spellOptional = invocations.stream().filter(spell -> fighter.canLaunchSpell(spell.getTemplate().getId()) && spell.getActionPointCost() <= fighter.getCurrentActionPoint()).findFirst();
        return spellOptional.isPresent() ? spellOptional.get() : null;
    }

    protected static short tryToAttack(Fight fight, Fighter fighter, List<Spell> spells) {
        List<Fighter> targets = CollectionQuery.from(fight.fighters()).filter(target -> target.getTeam().getId() != fighter.getTeam().getId()).orderBy(Fighter.compareByLife()).computeList(new ArrayList<>());

        AtomicReference<Spell> spell = new AtomicReference<>();
        AtomicReference<Fighter> target = new AtomicReference<>();

        targets.forEach(enemy -> {
            if (target.get() == null) {
                Spell currentSpell = getBestSpellForTarget(fighter, enemy, fighter.getFightCell().getId(), spells);
                spell.set(currentSpell);

                if (currentSpell != null)
                    target.set(enemy);
            }
        });

        AtomicInteger currentTarget = new AtomicInteger(0);
        AtomicReference<Short> currentCell = new AtomicReference<>((short) 0);
        AtomicReference<Spell> secondSpell = new AtomicReference<>();

        spells.forEach(currentSpell -> {
            int targetValue = getBestTargetZone(fight, fighter, currentSpell, fighter.getFightCell().getId(), false);
            if (targetValue != -1 && targetValue != 0) {
                short targetCount = (short) (targetValue / 1000);
                short cellId = (short) (targetValue - targetCount * 1000);
                if (targetCount > currentTarget.get()) {
                    currentTarget.set(targetCount);
                    currentCell.set(cellId);
                    secondSpell.set(currentSpell);
                }
            }
        });

        if (currentTarget.get() > 0 && currentCell.get() >= 15 && currentCell.get() <= 463 && secondSpell.get() != null) {
            secondSpell.get().applyToFight(fighter, fight.getFightMap().getCells().get(currentCell.get()));
            return secondSpell.get().getTemplate().getDuration();
        } else {
            if (target.get() == null || spell.get() == null)
                return 0;
            spell.get().applyToFight(fighter, target.get().getFightCell());
            return spell.get().getTemplate().getDuration();
        }
    }

    private static Spell getBestSpellForTarget(Fighter fighter, Fighter target, short cell, List<Spell> spells) {
        final AtomicReference<Short> maxInflation = new AtomicReference<>((short) 0);
        final AtomicReference<Spell> currentSpell = new AtomicReference<>();

        spells.forEach(spell -> {
            final AtomicReference<Short> currentInflation = new AtomicReference<>((short) 0), firstInflation = new AtomicReference<>((short) 0), secondInflation = new AtomicReference<>((short) 0);

            byte actionPoint = fighter.getCurrentActionPoint();

            byte[] usedActionPoint = {0, 0};

            if (canCastSpell1(fighter, spell, target.getFightCell(), cell)) {
                currentInflation.set(getInflation(spell));

                if (currentInflation.get() != 0) {
                    if (currentInflation.get() > maxInflation.get()) {
                        currentSpell.set(spell);
                        usedActionPoint[0] = spell.getActionPointCost();
                        firstInflation.set(currentInflation.get());
                        maxInflation.set(currentInflation.get());
                    }
                }

                new ArrayList<>(spells).forEach(secondSpell -> {
                    if ((actionPoint - usedActionPoint[0]) >= secondSpell.getActionPointCost())
                        return;

                    if (!canCastSpell1(fighter, secondSpell, target.getFightCell(), cell))
                        return;

                    firstInflation.set(getInflation(secondSpell));

                    if (firstInflation.get() == 0)
                        return;

                    if ((firstInflation.get() + currentInflation.get()) > maxInflation.get()) {
                        currentSpell.set(spell);
                        usedActionPoint[1] = secondSpell.getActionPointCost();
                        secondInflation.set(currentInflation.get());
                        maxInflation.set((short) (currentInflation.get() + secondInflation.get()));
                    }

                    spells.forEach(thirdSpell -> {
                        if ((actionPoint - usedActionPoint[0] - usedActionPoint[1]) < thirdSpell.getActionPointCost())
                            return;

                        if (!canCastSpell1(fighter, thirdSpell, target.getFightCell(), cell))
                            return;

                        currentInflation.set(getInflation(thirdSpell));

                        if (currentInflation.get() != 0 && (currentInflation.get() + firstInflation.get() + secondInflation.get()) > maxInflation.get()) {
                            currentSpell.set(spell);
                            maxInflation.set((short) (currentInflation.get() + firstInflation.get() + secondInflation.get()));
                        }

                    });
                });

            }

        });

        return currentSpell.get();
    }

    private static short getInflation(Spell spell) {
        AtomicReference<Short> inflation = new AtomicReference<>((short) 0);

        spell.getEffects().forEach(spellEffect -> {
            if (Utils.range(spellEffect.getType().value(), 96, 99))
                inflation.set((short) (inflation.get() + 500 * spellEffect.getDice().middle()));
            else
                inflation.set((short) (inflation.get() + spellEffect.getDice().middle()));
        });

        return inflation.get();
    }

    private static boolean canCastSpell1(Fighter caster, Spell spell, Cell cell, short targetCell) {
        Fight fight = caster.getFight();
        Fighter current = fight.getTurnList().getCurrent().getFighter();

        short casterCell = targetCell <= -1 ? caster.getFightCell().getId() : targetCell;


        if (spell == null) { //cheat ?
            caster.send(GamePacketFormatter.noActionMessage());
            caster.send(MessageFormatter.notHaveSpellMessage());
            return false;
        }

        if (current.getId() != caster.getId()) {
            caster.send(MessageFormatter.cannotLaunchSpellMessage());
            return false;
        }

        int usedPA = spell.getActionPointCost();

        if (caster.getCurrentActionPoint() < usedPA) {
            caster.send(MessageFormatter.needMoreActionPointMessage(caster.getCurrentActionPoint(), spell.getActionPointCost()));
            return false;
        }

        if (cell == null) {
            caster.send(MessageFormatter.busyCellMessage());
            return false;
        }

        if (spell.isInline() && !Cells.inSameLine(caster.getLocation().getMap().getWidth(), casterCell, cell.getId(), (short) 70)) {
            caster.send(MessageFormatter.notInlineMessage());
            return false;
        }

        OrientationEnum orientation = Cells.getOrientationByCells(casterCell, cell.getId(), caster.getLocation().getMap());

        if (spell.isPushEffect()) {
            if (!Cells.checkLineOfSide(caster.getLocation().getMap(), Cells.getCellIdByOrientation(casterCell, orientation, caster.getLocation().getMap().getWidth()), cell.getId())) {
                caster.send(MessageFormatter.obstacleOnLineMessage());
                return false;
            }
        }

        if (spell.isInline() && !Cells.checkLineOfSide(caster.getLocation().getMap(), casterCell, cell.getId())) {
            caster.send(MessageFormatter.obstacleOnLineMessage());
            return false;
        }

        byte distance = (byte) Cells.distanceBetween(caster.getLocation().getMap().getWidth(), casterCell, cell.getId());
        byte maximumRange = spell.getMaximumRange();
        byte minimumRange = spell.getMinimumRange();

        if (spell.isAdjustableRange()) {
            maximumRange += caster.getStatistics().get(CharacteristicType.RangePoints).total();
            if (maximumRange <= minimumRange)
                maximumRange = (byte) (minimumRange + 1);
        }

        if (maximumRange < minimumRange)
            maximumRange = minimumRange;

        if (distance < minimumRange || distance > maximumRange) {
            caster.send(MessageFormatter.badRangeMessage(maximumRange, maximumRange, distance));
            return false;
        }

        if (!caster.canLaunchSpell(spell.getTemplate().getId()))
            return false;

        int launchCount = spell.getMaxPerTurn();

        if (launchCount - caster.countOfLaunchedSpell(spell.getTemplate().getId()) <= 0 && launchCount > 0)
            return false;
        //TODO : per target
        return true;
    }

    private static int getBestTargetZone(Fight fight, Fighter fighter, Spell spell, short launchCell, boolean line) {
        if (spell.isMultipleZone() || spell.isInline() && !line)
            return 0;

        final List<Cell> targetCells = new ArrayList<>();

        if (spell.getMaximumRange() == 0)
            targetCells.add(fight.getFightMap().getCells().get(launchCell));
        else
            targetCells.addAll(new ArrayList<>(spell.zone().getCells(fighter.getLocation().getMap().getCells().get(launchCell), fighter)));


        final AtomicInteger targetCount = new AtomicInteger();
        final AtomicReference<Cell> selectedCell = new AtomicReference<>();

        targetCells.forEach(cell -> {
            if (canCastSpell1(fighter, spell, cell, launchCell)) {
                AtomicInteger currentTargetCount = new AtomicInteger();

                List<Cell> cells = new ArrayList<>(spell.zone().getCells(fighter.getLocation().getMap().getCells().get(cell.getId()), fighter));

                currentTargetCount.addAndGet((int) cells.stream().filter(targetCell -> targetCell.getFirstCreature() != 0 && fight.getFighter(targetCell.getFirstCreature()).getTeam().getId() != fighter.getTeam().getId()).count());

                if (currentTargetCount.get() > targetCount.get()) {
                    targetCount.set(currentTargetCount.get());
                    selectedCell.set(cell);
                }

            }
        });

        return targetCount.get() > 0 && selectedCell.get() != null ? selectedCell.get().getId() + targetCount.get() * 1000 : 0;
    }


}
