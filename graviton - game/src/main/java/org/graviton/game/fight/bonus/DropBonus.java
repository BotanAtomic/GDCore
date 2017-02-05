package org.graviton.game.fight.bonus;

import org.graviton.collection.CollectionQuery;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.drop.Drop;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.team.MonsterTeam;
import org.graviton.game.fight.team.PlayerTeam;
import org.graviton.game.items.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by Botan on 04/02/2017. 10:48
 */
public class DropBonus {
    private Map<Integer, List<Item>> items = new HashMap<>();

    public DropBonus(PlayerTeam winners, MonsterTeam team) {
        EntityFactory entityFactory = winners.getLeader().getCreature().entityFactory();
        short totalProspection = (short) winners.stream().mapToInt(Fighter::getProspection).sum();

        List<Fighter> sortedFighter = CollectionQuery.from(winners).orderBy(Fighter.compareByProspection()).reverse();
        List<Drop> sortedDrop = team.getMonsterGroup().allDrops(entityFactory);

        while (!sortedDrop.isEmpty()) {
            sortedFighter.forEach(player -> {
                if (!sortedDrop.isEmpty()) {
                    Drop currentDrop = sortedDrop.get(0);

                    if (currentDrop.validate((Player) player, totalProspection)) {
                        currentDrop.setAlreadyDropped(true);
                        if (items.containsKey(player.getId()))
                            add(player, currentDrop);
                        else {
                            items.put(player.getId(), new ArrayList<>());
                            items.get(player.getId()).add(entityFactory.getItemTemplate(currentDrop.getItem()).createRandom(entityFactory));
                        }
                    }
                    sortedDrop.remove(0);
                }
            });
        }
    }

    private void add(Fighter player, Drop drop) {
        Supplier<Stream<Item>> itemStream = () -> items.get(player.getId()).stream().filter(item -> item.getTemplate().getId() == drop.getItem());

        if (itemStream.get().count() > 0)
            itemStream.get().forEach(item -> item.changeQuantity((short) (item.getQuantity() + 1)));
        else
            items.get(player.getId()).add(player.getCreature().entityFactory().getItemTemplate(drop.getItem()).createRandom(((Player) player).getEntityFactory()));
    }

    List<Item> getItemsByPlayer(int playerId) {
        return items.getOrDefault(playerId, new ArrayList<>());
    }

}
