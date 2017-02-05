package org.graviton.database.repository;

import com.google.inject.Inject;
import org.graviton.database.Repository;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.maps.GameMap;
import org.graviton.xml.XMLElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by Botan on 13/11/2016 : 17:29
 */
public class GameMapRepository extends Repository<Integer, GameMap> {

    @Inject
    private EntityFactory entityFactory;

    private GameMap getByPosition(String position) {
        Optional<GameMap> record = super.stream().filter(gameMap -> gameMap.getPosition().equals(position)).findFirst();
        return record.isPresent() ? record.get().initialize() : null;
    }

    public int loadNpc(Document file) {
        return entityFactory.apply(file.getElementsByTagName("Npc"), element -> {
            GameMap gameMap = this.get(element.getAttribute("map").toInt());
            gameMap.addFuture(new Npc(entityFactory.getNpcTemplate(element.getAttribute("id").toInt()), gameMap, element));
        });
    }

    public int load(Document file) {
        NodeList list = file.getElementsByTagName("GameMap");

        IntStream.range(0, list.getLength()).forEach(i -> {
            XMLElement element = new XMLElement((Element) list.item(i));
            int id = element.getAttribute("id").toInt();
            short subArea = Short.parseShort(element.getElementByTagName("position").toString().split(",")[2]);
            GameMap gameMap = new GameMap(id, element, entityFactory);
            entityFactory.getSubArea(subArea).registerGameMap(gameMap);
            super.add(id, gameMap);
        });

        return objects.size();
    }

    public Collection<GameMap> getInitialized() {
        return super.stream().filter(GameMap::isInitialized).map(gameMap -> gameMap).collect(Collectors.toList());
    }

    @Override
    public GameMap find(Object value) {
        if (value instanceof Integer)
            return this.get((int) value).initialize();
        else
            return getByPosition((String) value);
    }
}
