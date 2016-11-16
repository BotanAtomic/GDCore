package org.graviton.game.client.player;

import lombok.Data;
import org.graviton.api.Creature;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.alignement.Alignement;
import org.graviton.game.breeds.AbstractBreed;
import org.graviton.game.breeds.models.Enutrof;
import org.graviton.game.client.account.Account;
import org.graviton.game.look.PlayerLook;
import org.graviton.game.maps.GameMap;
import org.graviton.game.maps.cell.Cell;
import org.graviton.game.position.Location;
import org.graviton.game.statistics.PlayerStatistics;
import org.graviton.network.game.protocol.PlayerProtocol;
import org.graviton.utils.StringUtils;
import org.jooq.Record;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 05/11/2016 : 22:57
 */
@Data
public class Player implements Creature {
    private final EntityFactory entityFactory;

    private final int id;
    private final Account account;

    private final PlayerLook look;
    private final PlayerStatistics statistics;
    private final Alignement alignement;
    private final String name;
    private Location location;
    private long kamas;

    /**
     * @param record        the record contains all data
     * @param account       owner account
     * @param entityFactory simple manager class
     */
    public Player(Record record, Account account, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.account = account;
        this.id = record.get(PLAYERS.ID);
        this.name = record.get(PLAYERS.NAME);

        this.look = new PlayerLook(record);
        this.statistics = new PlayerStatistics(record, (byte) (getBreed() instanceof Enutrof ? 120 : 100));
        this.alignement = new Alignement((byte) 0, 0, 0, false); //TODO : pvp
        this.location = new Location(entityFactory.getMap(record.get(PLAYERS.MAP)), record.get(PLAYERS.CELL));

        this.kamas = record.get(PLAYERS.KAMAS);
    }


    /**
     * Use for creation of player
     *
     * @param id            identification of player
     * @param data          data of creation
     * @param account       owner account
     * @param entityFactory simple manager class
     */
    public Player(int id, String data, Account account, EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        String[] informations = data.split("\\|");

        this.account = account;
        this.id = id;
        this.name = informations[0];

        this.look = new PlayerLook(StringUtils.parseColors(informations[3] + ";" + informations[4] + ";" + informations[5]), Byte.parseByte(informations[2]), AbstractBreed.get(Byte.parseByte(informations[1])));
        this.statistics = new PlayerStatistics((byte) (getBreed() instanceof Enutrof ? 120 : 100));
        this.alignement = new Alignement((byte) 0, 0, 0, false); //TODO : pvp
        this.location = new Location(entityFactory.getMap(getBreed().incarnamMap()), getBreed().incarnamCell());

        this.kamas = 0;
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

    public byte getOrientation() {
        return this.look.getOrientation();
    }

    public long getExperience() {
        return this.statistics.getExperience();
    }

    public short getLevel() {
        return this.statistics.getLevel();
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
}
