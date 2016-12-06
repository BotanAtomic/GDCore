package org.graviton.game.client.player;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignment.Alignment;
import org.graviton.game.breeds.AbstractBreed;
import org.graviton.game.breeds.models.Enutrof;
import org.graviton.game.client.account.Account;
import org.graviton.game.inventory.Inventory;
import org.graviton.game.items.Item;
import org.graviton.game.look.PlayerLook;
import org.graviton.game.look.enums.OrientationEnum;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.PlayerStatistics;
import org.graviton.network.game.protocol.GameProtocol;
import org.graviton.network.game.protocol.PlayerProtocol;
import org.graviton.utils.StringUtils;
import org.jooq.Record;

import java.util.Map;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 05/11/2016 : 22:57
 */
@Data
public class Player implements Creature {
    private final EntityFactory entityFactory;
    private final int id;
    private final Account account;
    private final String name;
    private final PlayerLook look;
    private final PlayerStatistics statistics;
    private final Alignment alignment;
    private final Inventory inventory;
    private boolean online = false;
    private Location location;

    public Player(Record record, Account account, Map<Integer, Item> items, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.account = account;
        this.id = record.get(PLAYERS.ID);
        this.name = record.get(PLAYERS.NAME);

        this.look = new PlayerLook(record);
        this.statistics = new PlayerStatistics(record, (byte) (getBreed() instanceof Enutrof ? 120 : 100));
        this.alignment = new Alignment((byte) 0, 0, 0, false); //TODO : pvp
        this.location = new Location(entityFactory.getMap(record.get(PLAYERS.MAP)), record.get(PLAYERS.CELL), record.get(PLAYERS.ORIENTATION));
        this.inventory = new Inventory(this, record.get(PLAYERS.KAMAS), items);
    }

    public Player(int id, String data, Account account, EntityFactory entityFactory) {
        account.getClient().send(GameProtocol.startAnimationMessage());

        this.entityFactory = entityFactory;
        String[] information = data.split("\\|");

        this.account = account;
        this.id = id;
        this.name = information[0];

        this.look = new PlayerLook(StringUtils.parseColors(information[3] + ";" + information[4] + ";" + information[5], ";"), Byte.parseByte(information[2]), AbstractBreed.get(Byte.parseByte(information[1])));
        this.statistics = new PlayerStatistics((byte) (getBreed() instanceof Enutrof ? 120 : 100));
        this.alignment = new Alignment((byte) 0, 0, 0, false); //TODO : pvp
        this.location = new Location(entityFactory.getMap(getBreed().incarnamMap()), getBreed().incarnamCell(), (byte) 1);
        this.inventory = new Inventory(this);

        this.registerItem(entityFactory.getItemTemplate((short) 9191).createMax(entityFactory.getNextItemId()), true);
    }

    public int getColor(byte color) {
        return this.look.getColors()[color - 1];
    }

    public int[] getColors() {
        return this.look.getColors();
    }

    public AbstractBreed getBreed() {
        return this.look.getBreed();
    }

    public byte getTitle() {
        return this.look.getTitle();
    }

    public short getSkin() {
        return this.look.getSkin();
    }

    public short getSize() {
        return this.look.getSize();
    }

    public byte getSex() {
        return this.look.getSex();
    }

    public OrientationEnum getOrientation() {
        return this.location.getOrientation();
    }

    public long getExperience() {
        return this.statistics.getExperience();
    }

    public short getLevel() {
        return this.statistics.getLevel();
    }

    public short[] getPods() {
        return this.statistics.getPods();
    }

    public GameMap getGameMap() {
        return this.location.getGameMap();
    }

    public Cell getCell() {
        return this.location.getCell();
    }

    @Override
    public String getGm() {
        return PlayerProtocol.gmMessage(this);
    }

    @Override
    public void send(String data) {
        this.account.getClient().send(data);
    }

    public void changeMap(int newGameMapId, short newCell) {
        this.getGameMap().out(this);

        GameMap newGameMap = entityFactory.getMap(newGameMapId);

        this.location.setCell(newGameMap.getCells().get(newCell));
        newGameMap.load(this);
    }

    public void changeMap(GameMap newGameMap) {
        this.getGameMap().out(this);
        this.location.setCell(newGameMap.getCells().get(newGameMap.getRandomCell()));
        newGameMap.load(this);
    }

    public void removeItem(Item item) {
        this.inventory.getItems().remove(item.getId());
        removeDatabaseItem(item);
    }

    public void registerItem(Item item, boolean create) {
        this.inventory.addItem(item, create);
    }

    public void createItem(Item item) {
        this.entityFactory.getPlayerRepository().createItem(item, this.id);
    }

    public void removeDatabaseItem(Item item) {
        this.entityFactory.getPlayerRepository().removeItem(item);
    }

    public void updateItems() {
        this.entityFactory.getPlayerRepository().saveItems(this);
    }

    public void update() {
        this.entityFactory.getPlayerRepository().save();
    }
}
