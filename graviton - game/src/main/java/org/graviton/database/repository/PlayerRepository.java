package org.graviton.database.repository;

import com.google.inject.Inject;
import lombok.Getter;
import org.graviton.database.Database;
import org.graviton.database.Repository;
import org.graviton.database.api.LoginDatabase;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.merchant.Merchant;
import org.graviton.game.items.Item;
import org.graviton.game.items.StoreItem;
import org.graviton.game.maps.GameMap;
import org.graviton.game.spell.SpellView;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.exchange.ExchangeConnector;
import org.graviton.utils.Utils;
import org.jooq.Record;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.graviton.database.jooq.game.tables.Items.ITEMS;
import static org.graviton.database.jooq.game.tables.Spells.SPELLS;
import static org.graviton.database.jooq.login.tables.Players.PLAYERS;
import static org.graviton.database.jooq.game.tables.Merchant.MERCHANT;


/**
 * Created by Botan on 06/11/2016 : 12:51
 */
public class PlayerRepository extends Repository<Integer, Player> {
    private Database database;

    @Getter @Inject private EntityFactory entityFactory;

    @Inject public PlayerRepository(@LoginDatabase Database database) {
        this.database = database;
    }

    public static <T> BinaryOperator<T> throwingMerger() {
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
                PLAYERS.SAVEDLOCATION,
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
                        player.compileSavedLocation(),
                        (byte) ExchangeConnector.serverId).execute();

        createSpells(player);
        super.add(player.getId(), player);
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
            GameMap gameMap = entityFactory.getMap(record.get(PLAYERS.MAP));
            Player player = gameMap.getFightFactory().searchDisconnectedPlayer(record.get(PLAYERS.ID));

            if (player == null)
                player = new Player(record, account, loadItems(record.get(PLAYERS.ID)), loadStore(record), loadSpells(record.get(PLAYERS.ID)), entityFactory);
            else
                player.setAccount(account);

            super.add(player.getId(), player);
            return player;
        }).collect(Collectors.toList()));
    }


    private Map<Integer, Item> loadItems(int playerId) {
        return database.getResult(ITEMS, ITEMS.OWNER.equal(playerId), ITEMS.POSITION.between((byte)-2, (byte) 58)).stream().map(record -> new Item(record, entityFactory.getItemTemplate(record.get(ITEMS.TEMPLATE)))).collect(Collectors.toMap(Item::getId, p -> p, throwingMerger(), ConcurrentHashMap::new));
    }

    private List<StoreItem> loadStore(Record record) {
        String data = record.get(PLAYERS.STORE);

        if (data.isEmpty())
            return new ArrayList<>();

        return Stream.of(data.split(";")).map(entry -> {
            String[] argument = entry.split(":");
            return new StoreItem(loadItem(Integer.parseInt(argument[0])), Long.parseLong(argument[1]));
        }).collect(Collectors.toList());
    }

    public Item loadItem(int itemId) {
        Record record = database.getRecord(ITEMS, ITEMS.ID.equal(itemId));
        return new Item(record, entityFactory.getItemTemplate(record.get(ITEMS.TEMPLATE)));
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

    private void removeSpellView(Player player) {
        database.getDslContext().delete(SPELLS).where(SPELLS.OWNER.equal(player.getId())).execute();
    }

    public List<Merchant> loadMerchant(int gameMap) {
        return database.getResult(MERCHANT, MERCHANT.MAP.equal(gameMap)).map(record -> {
            entityFactory.getAccountRepository().load(record.get(MERCHANT.ID));
            return new Merchant(get(record.get(MERCHANT.ID)).getStore());
        });
    }

    public void addMerchant(Player player) {
        database.getDslContext().insertInto(MERCHANT, MERCHANT.ID, MERCHANT.MAP).values(player.getId(), player.getMap().getId()).execute();
    }

    public void removeMerchant(int player) {
        database.getDslContext().delete(MERCHANT).where(MERCHANT.ID.equal(player)).execute();
    }

    void unload(int player) {
        super.remove(player);
    }

    public int getNextId() {
        return database.getNextId(PLAYERS, PLAYERS.ID);
    }

    public void save() {
        super.stream().forEach(this::save);
    }

    public void save(Player player) {
        database.update(PLAYERS).set(PLAYERS.MAP, player.getMap().getId())

                .set(PLAYERS.MAP, player.getMap().getId())
                .set(PLAYERS.CELL, player.getCell().getId())
                .set(PLAYERS.ORIENTATION, (byte) player.getOrientation().ordinal())
                .set(PLAYERS.SAVEDLOCATION, player.compileSavedLocation())

                .set(PLAYERS.VITALITY, player.getStatistics().get(CharacteristicType.Vitality).base())
                .set(PLAYERS.WISDOM, player.getStatistics().get(CharacteristicType.Wisdom).base())
                .set(PLAYERS.STRENGTH, player.getStatistics().get(CharacteristicType.Strength).base())
                .set(PLAYERS.INTELLIGENCE, player.getStatistics().get(CharacteristicType.Intelligence).base())
                .set(PLAYERS.CHANCE, player.getStatistics().get(CharacteristicType.Chance).base())
                .set(PLAYERS.AGILITY, player.getStatistics().get(CharacteristicType.Agility).base())
                .set(PLAYERS.LIFE, player.getLife().getPercent())
                .set(PLAYERS.ENERGY, player.getStatistics().getEnergy())
                .set(PLAYERS.GUILD, player.getGuild() == null ? 0 : player.getGuild().getId())
                .set(PLAYERS.WAYPOINTS, Utils.parseZaaps(player.getZaaps()))

                .set(PLAYERS.EXPERIENCE, player.getExperience())
                .set(PLAYERS.LEVEL, player.getLevel())
                .set(PLAYERS.KAMAS, player.getInventory().getKamas())

                .set(PLAYERS.ALIGNMENT, player.getAlignment().getId())
                .set(PLAYERS.HONOR, player.getAlignment().getHonor())
                .set(PLAYERS.DISHONNOR, player.getAlignment().getDishonor())
                .set(PLAYERS.PVP_ENABLED, (byte) (player.getAlignment().isEnabled() ? 1 : 0))

                .set(PLAYERS.STORE, player.compileStore())

                .set(PLAYERS.SIZE, player.getSize())
                .set(PLAYERS.TITLE, player.getTitle())
                .set(PLAYERS.SPELL_POINTS, player.getStatistics().getSpellPoints())

                .set(PLAYERS.JOB, player.compileJobs())

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

    private void saveItems(Player player) {
        player.getInventory().values().forEach(item -> saveItem(item, player.getId()));
    }

    public void saveItem(Item item, int owner) {
        database.update(ITEMS)
                .set(ITEMS.OWNER, owner)
                .set(ITEMS.POSITION, item.getPosition().value())
                .set(ITEMS.QUANTITY, item.getQuantity())
                .set(ITEMS.STATISTICS, item.parseEffects()).where(ITEMS.ID.equal(item.getId())).execute();
    }

    public void save(Account account) {
        account.getPlayers().forEach(this::save);
    }

    private Player getByName(String name) {
        Optional<Player> record;
        return (record = super.stream().filter(player -> player.getName().equals(name)).findFirst()).isPresent() ? record.get() : null;
    }

    public void send(String data) {
        super.stream().filter(Player::isOnline).forEach(player -> player.send(data));
    }

    @Override
    public Player find(Object value) {
        if (value instanceof Integer)
            return super.get((int) value);
        else
            return getByName((String) value);
    }
}
