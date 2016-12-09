package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.exchange.ExchangeConnector;
import org.graviton.utils.Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.game.tables.Items.ITEMS;
import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 06/11/2016 : 12:51
 */
public class PlayerRepository {
    private final Map<Integer, Player> players;
    private final LoginDatabase database;
    @Inject
    private EntityFactory entityFactory;

    @Inject
    public PlayerRepository(@Named("database.login") AbstractDatabase database) {
        this.players = new ConcurrentHashMap<>();
        this.database = (LoginDatabase) database;
    }

    public void create(Player player) {
        database.getDslContext().insertInto(PLAYERS,
                PLAYERS.ID,
                PLAYERS.OWNER,
                PLAYERS.NAME,
                PLAYERS.BREED,
                PLAYERS.SEX,
                PLAYERS.SKIN,
                PLAYERS.COLORS,
                PLAYERS.MAP,
                PLAYERS.CELL,
                PLAYERS.SERVER).
                values(player.getId(),
                        player.getAccount().getId(),
                        player.getName(),
                        player.getBreed().id(),
                        player.getSex(),
                        player.getSkin(),
                        Utils.parseColors(player.getColors()),
                        player.getGameMap().getId(),
                        player.getCell().getId(),
                        (byte) ExchangeConnector.serverId).execute();
    }

    public void remove(Player player) {
        database.getDslContext().delete(PLAYERS).where(PLAYERS.ID.equal(player.getId())).execute();
        player.getAccount().getPlayers().remove(player);
        removeItem(player);
    }

    public Collection<Player> getPlayers(Account account) {
        return Collections.synchronizedList(database.getResult(PLAYERS, PLAYERS.OWNER.equal(account.getId())).stream().map(record -> {
            Player player = new Player(record, account, loadItems(record.get(PLAYERS.ID)), entityFactory);
            this.players.put(player.getId(), player);
            return player;
        }).collect(Collectors.toList()));
    }

    private Map<Integer, Item> loadItems(int playerId) {
        return database.getResult(ITEMS, ITEMS.OWNER.equal(playerId)).stream().map(record -> new Item(record, entityFactory.getItemTemplate(record.get(ITEMS.TEMPLATE)))).collect(Collectors.toMap(Item::getId, p -> p));
    }

    public void unload(int player) {
        this.players.remove(player);
    }

    public int getNextId() {
        return database.getNextId(PLAYERS, PLAYERS.ID);
    }

    public void save() {
        this.players.values().forEach(this::save);
    }

    public void save(Player player) {
        database.update(PLAYERS).set(PLAYERS.MAP, player.getGameMap().getId())

                .set(PLAYERS.MAP, player.getGameMap().getId())
                .set(PLAYERS.CELL, player.getCell().getId())
                .set(PLAYERS.ORIENTATION, (byte) player.getOrientation().ordinal())

                .set(PLAYERS.VITALITY, player.getStatistics().get(CharacteristicType.Vitality).base())
                .set(PLAYERS.WISDOM, player.getStatistics().get(CharacteristicType.Wisdom).base())
                .set(PLAYERS.STRENGTH, player.getStatistics().get(CharacteristicType.Strength).base())
                .set(PLAYERS.INTELLIGENCE, player.getStatistics().get(CharacteristicType.Intelligence).base())
                .set(PLAYERS.CHANCE, player.getStatistics().get(CharacteristicType.Chance).base())
                .set(PLAYERS.AGILITY, player.getStatistics().get(CharacteristicType.Agility).base())

                .set(PLAYERS.SIZE, player.getSize())
                .set(PLAYERS.TITLE, player.getTitle())
                .set(PLAYERS.SPELL_POINTS, player.getStatistics().getSpellPoints())
                .set(PLAYERS.STAT_POINTS, player.getStatistics().getStatisticPoints()).execute();
        saveItems(player);
    }

    public void removeItem(Item item) {
        database.getDslContext().delete(ITEMS).where(ITEMS.ID.equal(item.getId())).execute();
    }

    private void removeItem(Player player) {
        database.getDslContext().delete(ITEMS).where(ITEMS.OWNER.equal(player.getId())).execute();
    }

    public void createItem(Item item, int owner) {
        database.getDslContext().insertInto(ITEMS, ITEMS.ID, ITEMS.TEMPLATE, ITEMS.OWNER, ITEMS.POSITION, ITEMS.QUANTITY, ITEMS.STATISTICS).
                values(item.getId(), item.getTemplate().getId(), owner, item.getPosition().value(), item.getQuantity(), item.parseEffects()).execute();
    }

    public void saveItems(Player player) {
        player.getInventory().getItems().values().forEach(item -> database.update(ITEMS)
                .set(ITEMS.POSITION, item.getPosition().value())
                .set(ITEMS.QUANTITY, item.getQuantity())
                .set(ITEMS.STATISTICS, item.parseEffects()).where(ITEMS.ID.equal(item.getId())).execute());
    }

    public void save(Account account) {
        account.getPlayers().forEach(this::save);
    }

    public Player find(String name) {
        Optional<Player> record;
        return (record = this.players.values().stream().filter(player -> player.getName().equals(name)).findFirst()).isPresent() ? record.get() : null;
    }

    public void send(String data) {
        this.players.values().stream().filter(Player::isOnline).forEach(player -> player.send(data));
    }
}
