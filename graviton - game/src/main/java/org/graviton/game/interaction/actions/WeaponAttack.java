package org.graviton.game.interaction.actions;

import org.graviton.game.client.player.Player;
import org.graviton.game.effect.enums.DamageType;
import org.graviton.game.effect.type.damage.DamageEffect;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.common.FightAction;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.maps.AbstractMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.utils.Cells;
import org.graviton.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Botan on 14/01/2017. 21:07
 */
public class WeaponAttack implements AbstractGameAction {
    private final short cell;
    private final Player player;

    public WeaponAttack(Player player, short cell) {
        this.cell = cell;
        this.player = player;
    }

    @Override
    public boolean begin() {
        Item item = player.getInventory().getByPosition(ItemPosition.Weapon);
        Fight fight = player.getFight();

        byte actionPointCost = 4;

        if (item != null) {
            ItemTemplate template = item.getTemplate();
            actionPointCost = template.getActionPointCost();

            if (Utils.random(1, player.getRate(template.getFailureRate(), false)) == 1) {  // Critical failure !
                fight.send(FightPacketFormatter.actionMessage(FightAction.WEAPON_CRITICAL_FAILURE, player.getId(), item.getId()));
                player.getTurn().end();
            } else {
                AtomicInteger criticalBonus = new AtomicInteger(0);

                Collection<Fighter> targets = targets(template);

                fight.send(FightPacketFormatter.actionMessage(FightAction.WEAPON_ATTACK, player.getId(), cell));

                if (Utils.random(1, player.getRate(template.getCriticalRate(), true)) == 1) { // Critical success !
                    fight.send(FightPacketFormatter.actionMessage(FightAction.CRITICAL_SPELL, player.getId(), "0"));
                    criticalBonus.set(template.getCriticalBonus());
                }

                template.getWeaponEffects().forEach(weaponEffect -> weaponEffect.effect().apply(player, targets, null, weaponEffect.build(template, criticalBonus.shortValue())));
            }

        } else {
            fight.send(FightPacketFormatter.actionMessage(FightAction.WEAPON_ATTACK, player.getId(), cell));
            new DamageEffect(DamageType.NEUTRAL).applyWeapon(player, simpleTarget(player.getMap().getCells().get(this.cell)));
        }

        player.setCurrentActionPoint((byte) (player.getCurrentActionPoint() - actionPointCost));
        fight.send(FightPacketFormatter.actionPointEventMessage(player.getId(), (byte) (actionPointCost * -1)));

        return false;
    }

    @Override
    public void cancel(String data) {

    }

    @Override
    public void finish(String data) {

    }

    private Collection<Fighter> targets(ItemTemplate item) {
        Collection<Fighter> targets = new ArrayList<>();

        if (item.getScopeRange()[1] <= 1)
            switch (item.getType()) {
                case Hammer:
                    hammerTargets(targets);
                    break;
                case Staff:
                case Wand:
                    wandTargets(targets);
                    break;
            }
        targets.add(simpleTarget(player.getMap().getCells().get(this.cell)));
        return targets.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Collection<Fighter> hammerTargets(Collection<Fighter> targets) {
        AbstractMap map = player.getMap();
        targets.add(simpleTarget(map.getCells().get(Cells.getCellIdByOrientation(cell, player.getOrientation(), map.getWidth()))));
        targets.add(simpleTarget(map.getCells().get(Cells.getCellIdByOrientation(player.getFightCell().getId(), player.getOrientation().next(), map.getWidth()))));
        targets.add(simpleTarget(map.getCells().get(Cells.getCellIdByOrientation(player.getFightCell().getId(), player.getOrientation().previous(), map.getWidth()))));
        return targets;
    }

    private Collection<Fighter> wandTargets(Collection<Fighter> targets) {
        AbstractMap map = player.getMap();
        targets.add(simpleTarget(map.getCells().get(Cells.getCellIdByOrientation(player.getFightCell().getId(), player.getOrientation().next(), map.getWidth()))));
        targets.add(simpleTarget(map.getCells().get(Cells.getCellIdByOrientation(player.getFightCell().getId(), player.getOrientation().previous(), map.getWidth()))));
        return targets;
    }

    private Fighter simpleTarget(Cell cell) {
        return player.getFight().getFighter(cell.getFirstCreature());
    }
}
