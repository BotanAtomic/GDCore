package org.graviton.game.fight.turn;

import com.google.common.collect.Lists;
import lombok.Data;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static org.graviton.collection.CollectionQuery.from;


/**
 * Created by Botan on 21/12/2016. 21:42
 */
@Data
public class FightTurnList {
    private final List<FightTurn> turns = Collections.synchronizedList(new LinkedList<>());

    private FightTurn current;

    public FightTurnList(Fight fight) {
        List<FightTurn> firstTeam = Lists.reverse(from(fight.getFirstTeam()).orderBy(Fighter.compareByInitiative()).transform(input -> new FightTurn(fight, input))
                .computeList(new LinkedList<>()));

        List<FightTurn> secondTeam = Lists.reverse(from(fight.getSecondTeam()).orderBy(Fighter.compareByInitiative()).transform(input -> new FightTurn(fight, input))
                .computeList(new LinkedList<>()));


        int size = firstTeam.size() > secondTeam.size() ? firstTeam.size() : secondTeam.size();

        if (firstTeam.get(0).getFighter().getInitiative() > secondTeam.get(0).getFighter().getInitiative())
            initialize(firstTeam, secondTeam, size);
        else
            initialize(secondTeam, firstTeam, size);

        current = turns.get(0);
    }

    public void remove(Fighter fighter) {
        if (current.equals(fighter.getTurn()))
            current.end();
        this.turns.remove(fighter.getTurn());
    }

    FightTurn next() {
        turns.remove(0);
        turns.add(current);

        return (current = turns.get(0));
    }

    private void initialize(List<FightTurn> first, List<FightTurn> second, int size) {
        IntStream.range(0, size).forEach(i -> {
            if (first.size() > i)
                turns.add(first.get(i));
            if (second.size() > i)
                turns.add(second.get(i));
        });
    }
}
