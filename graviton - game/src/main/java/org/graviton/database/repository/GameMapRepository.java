package org.graviton.database.repository;

import com.google.inject.Inject;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.maps.GameMap;
import org.graviton.xml.XMLElement;
import org.graviton.xml.XMLFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by Botan on 13/11/2016 : 17:29
 */
public class GameMapRepository {
    private final Map<Integer, GameMap> maps;

    @Inject
    private EntityFactory entityFactory;

    @Inject
    public GameMapRepository() {
        this.maps = new ConcurrentHashMap<>();
    }

    public GameMap get(int id) {
        return maps.get(id).initialize();
    }

    public GameMap getByPosition(String position) {
        Optional<GameMap> record = this.maps.values().stream().filter(gameMap -> gameMap.getPosition().equals(position)).findFirst();
        return record.isPresent() ? record.get().initialize() : null;
    }

    public int loadNpc(XMLFile file) {
        Collection<XMLElement> elements = file.getElementsByTagName("Npc");
        elements.forEach(element -> {
            GameMap gameMap = this.maps.get(element.getAttribute("map").toInt());
            gameMap.addFuture(new Npc(entityFactory.getNpcTemplate(element.getAttribute("id").toInt()), gameMap, element));
        });
        return elements.size();
    }

    public int load(Document file) {
        NodeList list = file.getElementsByTagName("GameMap");
        IntStream.range(0, list.getLength()).forEach(i -> {
            XMLElement element = new XMLElement((Element) list.item(i));
            int id = element.getAttribute("id").toInt();
            short subArea = Short.parseShort(element.getElementByTagName("position").toString().split(",")[2]);
            GameMap gameMap = new GameMap(id, element, entityFactory);
            entityFactory.getSubArea(subArea).registerGameMap(gameMap);
            this.maps.put(id, gameMap);
        });
        return maps.size();
    }

    public Collection<GameMap> getInitialized() {
        return this.maps.values().stream().filter(GameMap::isInitialized).map(gameMap -> gameMap).collect(Collectors.toList());
    }
}
