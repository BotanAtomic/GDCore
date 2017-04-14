package org.graviton.database.repository;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.graviton.database.Repository;
import org.graviton.game.fight.Fighter;
import org.graviton.game.intelligence.api.ArtificialIntelligence;
import org.graviton.game.intelligence.api.Intelligence;
import org.graviton.game.intelligence.artificial.PassIntelligence;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.graviton.utils.FastClassLoader.getClasses;

/**
 * Created by Botan on 15/01/2017. 16:01
 */

@Slf4j
public class ArtificialIntelligenceRepository extends Repository<Pair<Byte, Byte>, Class<? extends ArtificialIntelligence>> {

    public int load() {
        for (Class<? extends ArtificialIntelligence> clazz : getClasses("org.graviton.game.intelligence.artificial", ArtificialIntelligence.class))
            add(clazz);

        return super.objects.size();
    }

    private void add(Class<? extends ArtificialIntelligence> intelligence) {
        Intelligence annotation = intelligence.getAnnotation(Intelligence.class);
        add(new Pair<>(annotation.value(), annotation.repetition()), intelligence);
    }

    public ArtificialIntelligence create(byte intelligence, Fighter fighter) {
        try {
            Optional<Pair<Byte, Byte>> result = super.objects.keySet().stream().filter(intelligenceData -> intelligenceData.getKey() == intelligence).findAny();

            if (result.isPresent())
                return get(result.get()).getDeclaredConstructor(Fighter.class).newInstance(fighter).setRepetition(result.get().getValue());
            else
                throw new IllegalAccessException("Result is null");

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return new PassIntelligence(fighter);
        }
    }

    @Override
    public Class<? extends ArtificialIntelligence> find(Object value) {
        return null;
    }
}
