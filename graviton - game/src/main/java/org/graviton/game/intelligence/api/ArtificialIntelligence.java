package org.graviton.game.intelligence.api;

import javafx.util.Pair;
import org.graviton.collection.CollectionQuery;
import org.graviton.game.effect.state.State;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.turn.FightTurn;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.interaction.actions.FightMovement;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.maps.fight.FightMap;
import org.graviton.game.paths.MonsterPath;
import org.graviton.game.spell.Spell;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.utils.Cells;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Botan on 18/01/2017. 23:30
 */
public abstract class ArtificialIntelligence {
    protected final Fighter fighter;
    protected final FightTurn fightTurn;
    protected boolean attack;

    private byte repetition;

    protected ArtificialIntelligence(Fighter fighter) {
        this.fighter = fighter;
        this.fightTurn = fighter.getTurn();

    }

    public ArtificialIntelligence setRepetition(byte repetition) {
        this.repetition = repetition;
        return this;
    }

    public void start() {
        short result = run();

        repetition--;
        if (repetition <= 0 || (fighter.getCurrentActionPoint() == 0 && fighter.getCurrentMovementPoint() == 0))
            fighter.getFight().schedule(() -> fighter.getTurn().end(true), result);
        else
            fighter.getFight().schedule(this::start, result);
    }

    public abstract short run();

    protected static List<Fighter> getNearestInlineEnemy(Fighter fighter) {
        List<Fighter> fighters = new ArrayList<>();
        AbstractMap abstractMap = fighter.getFight().getFightMap();
        fighter.getFight().fighters().stream().filter(target -> target.getTeam().getSide().ordinal() != fighter.getTeam().getSide().ordinal()).filter(target ->
                target.isAlive() && target.isVisible() && Cells.inSameLine(abstractMap.getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId(), (short) 70)
                        && Cells.distanceBetween(abstractMap.getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId()) > 1).forEach(fighters::add);
        return fighters;
    }

    protected static Fighter getNearestSoulInlineEnemy(Fight fight, Fighter fighter) {
        AtomicInteger distanceRequired = new AtomicInteger(1000);
        AtomicReference<Fighter> current = new AtomicReference<>();
        fight.fighters().stream().filter(target -> target.getTeam().getSide().ordinal() != fighter.getTeam().getSide().ordinal()).filter(target ->
                target.isAlive() && target.isVisible() && Cells.inSameLine(fight.getFightMap().getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId(), (short) 70)
        && target.hasState(State.Drunk)).forEach(target -> {
            int distance = Cells.distanceBetween(fight.getFightMap().getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId());
            if (distance < distanceRequired.get()) {
                distanceRequired.set(distance);
                current.set(target);
            }
        });
        return current.get();
    }

    protected static Fighter getNearestEnemy(Fight fight, Fighter fighter) {
        AtomicInteger distanceRequired = new AtomicInteger(1000);
        AtomicReference<Fighter> current = new AtomicReference<>();
        fight.fighters().stream().filter(target -> target.getTeam().getSide().ordinal() != fighter.getTeam().getSide().ordinal()).filter(target -> target.isAlive() && target.isVisible()).forEach(target -> {
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
        fight.fighters().stream().filter(target -> target.getTeam().getId() != fighter.getTeam().getId()).filter(target -> target.isVisible() && target.isAlive()).forEach(target -> {
            int distance = Cells.distanceBetween(fight.getFightMap().getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId());
            if (distance < distanceRequired.get() && distance > minimumRange) {
                distanceRequired.set(distance);
                current.set(target);
            }
        });
        return current.get();
    }

    protected static Fighter getNearestInvocation(Fight fight, Fighter fighter, byte minimumRange, byte maximumRange) {
        AtomicInteger distanceRequired = new AtomicInteger(maximumRange);
        AtomicReference<Fighter> current = new AtomicReference<>();
        fight.fighters().stream().filter(target -> target.getTeam().getId() == fighter.getTeam().getId()).filter(target -> target.isVisible() && target.isAlive() && target.isInvocation()).forEach(target -> {
            int distance = Cells.distanceBetween(fight.getFightMap().getWidth(), fighter.getFightCell().getId(), target.getFightCell().getId());
            if (distance < distanceRequired.get() && distance > minimumRange) {
                distanceRequired.set(distance);
                current.set(target);
            }
        });
        return current.get();
    }

    protected static short tryToMove(Fight fight, Fighter fighter, Fighter initialTarget, boolean diagonal, byte movementLimit) {
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
                short newCell = diagonal ? Cells.getNearestCellDiagonal(map, cell.getId(), target.getFightCell().getId()) : Cells.getNearestCellAround(map, target.getFightCell().getId(), cell.getId());

                if (newCell != -1 && foundCell.get() == -1)
                    foundCell.set(newCell);
            });
        }

        System.err.println("Found cell = " + foundCell.get());
        return move(fighter, foundCell.get(), movementLimit);
    }

    protected static short move(Fighter fighter, short destination, byte limit) {
        Cell cell = fighter.getFightCell();
        List<Cell> path = new MonsterPath(fighter.getFight().getFightMap(), cell.getId(), destination).getShortestPath();

        if (path == null || path.isEmpty())
            return 0;


        List<Cell> finalPath = new LinkedList<>();

        for (int a = 0; a < fighter.getCurrentMovementPoint(); a++) {
            if (path.size() == a || (limit != 0 && a == limit))
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

    protected static boolean tryToInvoke(Fight fight, Fighter fighter, List<Spell> invocations, boolean far) {
        if (fighter.getInvocations().size() >= fighter.getStatistics().get(CharacteristicType.Summons).total())
            return false;

        Fighter firstTarget = getNearestEnemy(fight, fighter);

        if (firstTarget == null)
            return false;


        Spell spell = getBestInvocationSpell(fighter, invocations);

        if (spell == null)
            return false;

        short firstCell = far ? Cells.getNearestCellAround(fight.getFightMap(), fighter.getFightCell().getId(), fighter.getFightCell().getId()) : Cells.getCellBetweenEnemy(fighter.getFightCell().getId(), fight.getFightMap());

        if (firstCell == -1)
            return false;


        spell.applyToFight(fighter, fight.getFightMap().getCells().get(firstCell));
        return true;
    }

    private static Spell getBestInvocationSpell(Fighter fighter, List<Spell> invocations) {
        Optional<Spell> spellOptional = invocations.stream().filter(spell -> fighter.canLaunchSpell(spell.getTemplate().getId(), spell.getMaxPerTurn(), (short) -1, (byte) -1) && spell.getActionPointCost() <= fighter.getCurrentActionPoint()).findFirst();
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


    protected static short tryToAttack(Fighter fighter, Fighter predicateTarget, List<Spell> spells) {
        Spell spell = getBestSpellForTarget(fighter, predicateTarget, fighter.getFightCell().getId(), spells);

        if (spell != null) {
            spell.applyToFight(fighter, predicateTarget.getFightCell());
            return spell.getTemplate().getDuration();
        }

        return 0;
    }


    protected static short tryToHeal(Fighter fighter, Fighter predicateTarget) {
        Spell spell = getBestHealSpell(fighter);

        if (spell != null) {
            spell.applyToFight(fighter, predicateTarget.getFightCell());
            return spell.getTemplate().getDuration();
        }

        return 0;
    }


    private static Spell getBestHealSpell(Fighter fighter) {
        AtomicReference<Spell> bestSpell = new AtomicReference<>();
        AtomicInteger bestInfluence = new AtomicInteger();

        fighter.getSpells().stream().filter(Spell::canHeal).forEach(spell -> {
            if (bestSpell.get() == null && canCastSpell1(fighter, spell, fighter.getFightCell(), fighter.getFightCell().getId())) {
                bestSpell.set(spell);
            } else {
                short currentInfluence = getInfluence(spell);
                if (currentInfluence > bestInfluence.get() && canCastSpell1(fighter, spell, fighter.getFightCell(), fighter.getFightCell().getId())) {
                    bestInfluence.set(currentInfluence);
                    bestSpell.set(spell);
                }
            }
        });

        return bestSpell.get();
    }


    protected boolean tryToBuff(Fighter fighter, Fighter target) {
        System.err.println("Try to buff");
        List<Spell> spells = fighter.getSpellFilter().getBuffs();
        AtomicReference<Spell> bestBuff = new AtomicReference<>();
        AtomicInteger bestInfluence = new AtomicInteger(-1);

        spells.forEach(spell -> {
            short influence = getInfluence(spell);
            if (influence > bestInfluence.get() && canCastSpell1(fighter, spell, fighter.getFightCell(), target.getFightCell().getId())) {
                bestInfluence.set(influence);
                bestBuff.set(spell);
            }

        });

        if (bestBuff.get() != null) {
            bestBuff.get().applyToFight(fighter, target.getFightCell());
            return true;
        }

        return false;
    }

    private static Spell getBestSpellForTarget(Fighter fighter, Fighter target, short cell, List<Spell> spells) {
        final AtomicReference<Short> maxInfluence = new AtomicReference<>((short) 0);
        final AtomicReference<Spell> currentSpell = new AtomicReference<>();


        spells.forEach(spell -> {
            final AtomicReference<Short> currentInfluence = new AtomicReference<>((short) 0), firstInfluence = new AtomicReference<>((short) 0), secondInfluence = new AtomicReference<>((short) 0);

            byte actionPoint = fighter.getCurrentActionPoint();

            byte[] usedActionPoint = {0, 0};

            if (canCastSpell1(fighter, spell, target.getFightCell(), cell)) {
                currentInfluence.set(getInfluence(spell));

                System.err.println("Spell " + spell.getTemplate().getId() + " inf = " + currentInfluence.get());

                if (currentInfluence.get() > maxInfluence.get()) {
                    currentSpell.set(spell);
                    usedActionPoint[0] = spell.getActionPointCost();
                    firstInfluence.set(currentInfluence.get());
                    maxInfluence.set(currentInfluence.get());
                }

                new ArrayList<>(spells).forEach(secondSpell -> {
                    if ((actionPoint - usedActionPoint[0]) >= secondSpell.getActionPointCost())
                        return;

                    if (!canCastSpell1(fighter, secondSpell, target.getFightCell(), cell))
                        return;

                    firstInfluence.set(getInfluence(secondSpell));

                    if ((firstInfluence.get() + currentInfluence.get()) > maxInfluence.get()) {
                        currentSpell.set(spell);
                        usedActionPoint[1] = secondSpell.getActionPointCost();
                        secondInfluence.set(currentInfluence.get());
                        maxInfluence.set((short) (currentInfluence.get() + secondInfluence.get()));
                    }

                    spells.forEach(thirdSpell -> {
                        if ((actionPoint - usedActionPoint[0] - usedActionPoint[1]) < thirdSpell.getActionPointCost())
                            return;

                        if (!canCastSpell1(fighter, thirdSpell, target.getFightCell(), cell))
                            return;

                        currentInfluence.set(getInfluence(thirdSpell));

                        if ((currentInfluence.get() + firstInfluence.get() + secondInfluence.get()) > maxInfluence.get()) {
                            currentSpell.set(spell);
                            maxInfluence.set((short) (currentInfluence.get() + firstInfluence.get() + secondInfluence.get()));
                        }

                    });
                });

            }

        });

        return currentSpell.get();
    }

    private static short getInfluence(Spell spell) {
        AtomicReference<Short> influence = new AtomicReference<>((short) 0);

        spell.getEffects().forEach(spellEffect -> influence.set((short) (influence.get() + spellEffect.getType().getInfluence() * spellEffect.getDice().middle() + 1)));

        return influence.get();
    }

    private static boolean canCastSpell1(Fighter caster, Spell spell, Cell cell, short targetCell) {
        if (!caster.isVisible() && spell.canCauseDamage())
            return false;

        Fight fight = caster.getFight();
        Fighter current = fight.getTurnList().getCurrent().getFighter();

        short casterCell = targetCell <= -1 ? caster.getFightCell().getId() : targetCell;

        if (spell == null) {
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

        Fighter target = fight.getFighter(cell.getFirstCreature());
        return caster.canLaunchSpell(spell.getTemplate().getId(), spell.getMaxPerTurn(), (target == null ? -1 : target.getId()), spell.getMaxPerPlayer());
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
