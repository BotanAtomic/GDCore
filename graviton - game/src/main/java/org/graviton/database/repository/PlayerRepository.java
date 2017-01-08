package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.spell.SpellView;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.exchange.ExchangeConnector;
import org.graviton.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.game.tables.Items.ITEMS;
import static org.graviton.database.jooq.game.tables.Spells.SPELLS;
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

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
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
                        player.getMap().getId(),
                        player.getCell().getId(),
                        (byte) ExchangeConnector.serverId).execute();

        createSpells(player);
        this.players.put(player.getId(), player);
    }

    private void createSpells(Player player) {
        player.getSpellList().forEach((id, viewer) -> createSpell(id, viewer, player));
    }

    public void createSpell(short id, SpellView spellView, Player player) {
        database.getDslContext().insertInto(SPELLS, SPELLS.ID, SPELLS.SPELL, SPELLS.LEVEL, SPELLS.POSITION, SPELLS.OWNER).
                values(id, spellView.getId(), spellView.getSpell().getLevel(), spellView.getPosition(), player.getId()).execute();
    }

    public void remove(Player player) {
        database.getDslContext().delete(PLAYERS).where(PLAYERS.ID.equal(player.getId())).execute();
        player.getAccount().getPlayers().remove(player);
        removeItem(player);
        removeSpellView(player);
    }

    public Collection<Player> getPlayers(Account account) {
        return Collections.synchronizedList(database.getResult(PLAYERS, PLAYERS.OWNER.equal(account.getId())).stream().map(record -> {
            Player player = new Player(record, account, loadItems(record.get(PLAYERS.ID)), loadSpells(record.get(PLAYERS.ID)), entityFactory);
            this.players.put(player.getId(), player);
            return player;
        }).collect(Collectors.toList()));
    }

    private Map<Integer, Item> loadItems(int playerId) {
        return database.getResult(ITEMS, ITEMS.OWNER.equal(playerId)).stream().map(record -> new Item(record, entityFactory.getItemTemplate(record.get(ITEMS.TEMPLATE)))).collect(Collectors.toMap(Item::getId, p -> p, throwingMerger(), ConcurrentHashMap::new));
    }

    private Map<Short, SpellView> loadSpells(int playerId) {
        return database.getResult(SPELLS, SPELLS.OWNER.equal(playerId)).stream().map(record ->
                new SpellView(record.get(SPELLS.ID), this.entityFactory.getSpellTemplate(record.get(SPELLS.SPELL))
                        .getLevel(record.get(SPELLS.LEVEL)), record.get(SPELLS.POSITION))
        ).collect(Collectors.toMap(SpellView::getPlace, s -> s, throwingMerger(), TreeMap::new));


    }

    public void saveSpellView(SpellView spellView, Player player) {
        database.update(SPELLS).set(SPELLS.LEVEL, spellView.getSpell().getLevel()).set(SPELLS.POSITION, spellView.getPosition())
                .where(SPELLS.OWNER.equal(player.getId()), SPELLS.SPELL.equal(spellView.getId())).execute();
    }

    public void removeSpellView(Player player) {
        database.getDslContext().delete(SPELLS).where(SPELLS.OWNER.equal(player.getId())).execute();
    }

    void unload(int player) {
        this.players.remove(player);
    }

    public int getNextId() {
        return database.getNextId(PLAYERS, PLAYERS.ID);
    }

    public void save() {
        this.players.values().forEach(this::save);
    }

    public void save(Player player) {
        database.update(PLAYERS).set(PLAYERS.MAP, player.getMap().getId())

                .set(PLAYERS.MAP, player.getMap().getId())
                .set(PLAYERS.CELL, player.getCell().getId())
                .set(PLAYERS.ORIENTATION, (byte) player.getOrientation().ordinal())

                .set(PLAYERS.VITALITY, player.getStatistics().get(CharacteristicType.Vitality).base())
                .set(PLAYERS.WISDOM, player.getStatistics().get(CharacteristicType.Wisdom).base())
                .set(PLAYERS.STRENGTH, player.getStatistics().get(CharacteristicType.Strength).base())
                .set(PLAYERS.INTELLIGENCE, player.getStatistics().get(CharacteristicType.Intelligence).base())
                .set(PLAYERS.CHANCE, player.getStatistics().get(CharacteristicType.Chance).base())
                .set(PLAYERS.AGILITY, player.getStatistics().get(CharacteristicType.Agility).base())

                .set(PLAYERS.EXPERIENCE, player.getExperience())
                .set(PLAYERS.LEVEL, player.getLevel())

                .set(PLAYERS.SIZE, player.getSize())
                .set(PLAYERS.TITLE, player.getTitle())
                .set(PLAYERS.SPELL_POINTS, player.getStatistics().getSpellPoints())
                .set(PLAYERS.STAT_POINTS, player.getStatistics().getStatisticPoints()).where(PLAYERS.ID.equal(player.getId())).execute();
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
        player.getInventory().values().forEach(item -> database.update(ITEMS)
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

    public Player get(int id) {
        System.err.println("Contains " + id + " ? " + this.players.containsKey(id));
        return this.players.get(id);
    }
}
