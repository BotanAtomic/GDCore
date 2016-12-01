package org.graviton.database.repository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.graviton.database.AbstractDatabase;
import org.graviton.database.LoginDatabase;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.statistics.common.CharacteristicType;
import org.graviton.network.exchange.ExchangeConnector;
import org.graviton.utils.StringUtils;
import org.jooq.UpdateSetFirstStep;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.graviton.database.jooq.login.tables.Players.PLAYERS;

/**
 * Created by Botan on 06/11/2016 : 12:51
 */
public class PlayerRepository {
    private final Map<Integer, Player> players;
    @Inject
    private EntityFactory entityFactory;
    private LoginDatabase database;

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
                        StringUtils.parseColors(player.getColors()),
                        player.getGameMap().getId(),
                        player.getCell().getId(),
                        (byte) ExchangeConnector.serverId).execute();
    }

    public void remove(Player player) {
        database.getDslContext().delete(PLAYERS).where(PLAYERS.ID.equal(player.getId())).execute();
        player.getAccount().getPlayers().remove(player);
    }

    public Collection<Player> getPlayers(Account account) {
        return Collections.synchronizedList(database.getResult(PLAYERS, PLAYERS.OWNER.equal(account.getId())).stream().map(record -> {
            Player player = new Player(record, account, entityFactory);
            this.players.put(player.getId(), player);
            return player;
        }).collect(Collectors.toList()));
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

    private void save(Player player) {
        UpdateSetFirstStep steps = database.update(PLAYERS);

        steps.set(PLAYERS.MAP, player.getGameMap().getId());
        steps.set(PLAYERS.CELL, player.getCell().getId());
        steps.set(PLAYERS.ORIENTATION, player.getOrientation().ordinal());

        steps.set(PLAYERS.VITALITY, player.getStatistics().get(CharacteristicType.Vitality).base());
        steps.set(PLAYERS.WISDOM, player.getStatistics().get(CharacteristicType.Wisdom).base());
        steps.set(PLAYERS.STRENGTH, player.getStatistics().get(CharacteristicType.Strength).base());
        steps.set(PLAYERS.INTELLIGENCE, player.getStatistics().get(CharacteristicType.Intelligence).base());
        steps.set(PLAYERS.CHANCE, player.getStatistics().get(CharacteristicType.Chance).base());
        steps.set(PLAYERS.AGILITY, player.getStatistics().get(CharacteristicType.Agility).base());

        steps.set(PLAYERS.SIZE, player.getSize());
        steps.set(PLAYERS.TITLE, player.getTitle());
        steps.set(PLAYERS.SPELL_POINTS, player.getStatistics().getSpellPoints());
        steps.set(PLAYERS.STAT_POINTS, player.getStatistics().getStatisticPoints()).execute();
    }

    public void save(Account account) {
        account.getPlayers().forEach(this::save);
    }
}
