package org.graviton.game.client.account;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.database.repository.PlayerRepository;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.trunk.type.Bank;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.utils.Utils;
import org.jooq.Record;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.graviton.database.jooq.login.tables.Accounts.ACCOUNTS;

/**
 * Created by Botan on 05/11/2016 : 01:04
 */
@Data
public class Account {
    private final int id;
    private final String question, answer, nickname;

    private byte rights;
    private String channels;
    private String lastConnection, lastAddress;

    private boolean friendNotification;

    private Collection<Player> players;

    private List<Integer> friends, enemies;

    private GameClient client;

    private String cachedPlayerPacket;

    private Bank bank;

    private List<Pair<ItemTemplate, Short>> gifts;

    public Account(Record record, PlayerRepository playerRepository) {
        this.id = record.get(ACCOUNTS.ID);
        this.question = record.get(ACCOUNTS.QUESTION);
        this.answer = record.get(ACCOUNTS.ANSWER);
        this.nickname = record.get(ACCOUNTS.NICKNAME);
        this.rights = record.get(ACCOUNTS.RIGHTS);
        this.channels = record.get(ACCOUNTS.CHANNELS);
        this.lastConnection = record.get(ACCOUNTS.LAST_CONNECTION);
        this.lastAddress = record.get(ACCOUNTS.LAST_ADDRESS);
        this.friendNotification = record.get(ACCOUNTS.FRIEND_NOTIFICATION_LISTENER) > 0;
        this.cachedPlayerPacket = PlayerPacketFormatter.playersPacketMessage(this.players = playerRepository.getPlayers(this));

        String gifts = record.get(ACCOUNTS.GIFTS);
        if(!gifts.isEmpty()) {
            this.gifts = Stream.of(gifts.split(";")).map(itemData -> {
                short[] values = Utils.shortSplit(itemData, ",");
                return new Pair<>(playerRepository.getEntityFactory().getItemTemplate(values[0]), values[1]);
            }).collect(Collectors.toList());
        }
    }

    public String getPlayerPacket(boolean useCache) {
        if (useCache)
            return cachedPlayerPacket;
        return PlayerPacketFormatter.playersPacketMessage(this.players);
    }

    public Player getPlayer(int playerId) {
        Optional<Player> record;
        return (record = this.players.stream().filter(player -> player.getId() == playerId).findFirst()).isPresent() ? record.get() : null;
    }

    public Pair<ItemTemplate, Short> getGift(int itemTemplate) {
        return gifts.stream().filter(pair -> pair.getKey().getId() == itemTemplate).findAny().orElse(null);
    }

    public String compileGifts() {
        if(this.gifts == null || this.gifts.isEmpty())
            return "";

        StringBuilder builder = new StringBuilder();
        this.gifts.forEach(pair -> builder.append(pair.getKey().getId()).append(",").append(pair.getValue()).append(";"));
        return builder.substring(0, builder.length() - 1);
    }

}
