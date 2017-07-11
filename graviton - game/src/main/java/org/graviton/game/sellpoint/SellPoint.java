package org.graviton.game.sellpoint;

import javafx.util.Pair;
import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.database.repository.AccountRepository;
import org.graviton.game.client.account.Account;
import org.graviton.game.client.player.Player;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.network.game.protocol.ItemPacketFormatter;
import org.graviton.network.game.protocol.MessageFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.network.game.protocol.SellPointPacketFormatter;
import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.graviton.database.repository.PlayerRepository.throwingMerger;

/**
 * Created by Botan on 26/06/17. 22:56
 */

@Data
public class SellPoint {
    private final EntityFactory entityFactory;

    private final byte id, tax;
    private final short expiration, level;
    private final String categoriesData;
    private final Map<Byte, SellPointCategory> categories;
    private final Map<Integer, Pair<Byte, Short>> path;

    private DecimalFormat pattern = new DecimalFormat("0.0");

    private short gameMap;


    public SellPoint(XMLElement element, EntityFactory entityFactory) {
        this.id = element.getAttribute("id").toByte();
        this.tax = element.getAttribute("tax").toByte();
        this.expiration = element.getAttribute("expiration").toShort();
        this.level = element.getAttribute("level").toShort();
        this.categoriesData = element.getAttribute("categories").toString();
        this.gameMap = element.getAttribute("map").toShort();
        this.categories = Utils.arrayToByteList(categoriesData, ",").stream().
                map(b -> new SellPointCategory(b, entityFactory)).collect(Collectors.toMap(SellPointCategory::getId, o -> o, throwingMerger(), ConcurrentHashMap::new));
        this.path = new ConcurrentHashMap<>();

        this.entityFactory = entityFactory;
    }

    public String categoryTemplates(byte category) {
        return this.categories.get(category).parseTemplate();
    }

    public String getStringTax() {
        return this.pattern.format(this.tax).replace(",", ".");
    }

    public SellPointLine getLine(int lineId) {
        Pair<Byte, Short> result = this.path.get(lineId);

        if (result == null)
            return null;

        return this.categories.get(result.getKey()).getTemplate(result.getValue()).getLine(lineId);
    }

    public boolean add(SellPointItem sellPointItem, boolean load) {
        sellPointItem.setSellPoint(this.id);

        byte category = sellPointItem.getItem().getTemplate().getType().getValue();

        if (this.categories.get(category) != null) {
            this.categories.get(category).add(sellPointItem);
            this.path.put(sellPointItem.getLineId(), new Pair<>(category, sellPointItem.getItem().getTemplate().getId()));

            if (!load)
                entityFactory.saveSellPointItem(sellPointItem, gameMap);

            return true;
        }
        return false;
    }

    public void remove(SellPointItem sellPointItem) {
        this.categories.get(sellPointItem.getItem().getTemplate().getType().getValue()).remove(sellPointItem);
        this.path.remove(sellPointItem.getLineId());
        entityFactory.removeSellPointItem(sellPointItem.getItem().getId());
    }

    private List<SellPointItem> all() {
        return categories.values().stream().map(SellPointCategory::all).flatMap(List::stream).collect(Collectors.toList());
    }

    public List<SellPointItem> itemsByPlayer(Player player) {
        return all().stream().filter(sellPointItem -> sellPointItem.getOwner() == player.getAccount().getId()).collect(Collectors.toList());
    }

    private int getFinalTax(long price) {
        return (int) (price * (((double) tax / 100)));
    }

    public void addItem(Player player, int itemId, byte quantity, long price) {
        int tax = getFinalTax(price);

        if (itemsByPlayer(player).size() > 100) {
            player.send(MessageFormatter.sellPointItemLimitMessage());
        } else if (tax > player.getInventory().getKamas()) {
            player.send(MessageFormatter.merchantTaxErrorMessage());
        } else {
            player.getInventory().addKamas(-tax);
            player.send(PlayerPacketFormatter.asMessage(player));

            Item item = player.getInventory().get(itemId);
            byte realQuantity = (byte) (quantity == 1 ? 1 : (quantity == 2 ? 10 : 100));
            short newQuantity = (short) (item.getQuantity() - realQuantity);

            if (newQuantity <= 0) {
                player.getInventory().remove(item.getId());
                player.send(ItemPacketFormatter.deleteMessage(item.getId()));
            } else {
                Item sliced = item.clone(entityFactory.getNextItemId());
                sliced.setQuantity(realQuantity);
                item.setQuantity(newQuantity);
                player.createItem(sliced);
                player.send(ItemPacketFormatter.quantityMessage(item.getId(), newQuantity));
                item = sliced;
            }

            item.setPosition(ItemPosition.SellPoint);
            entityFactory.getPlayerRepository().saveItem(item, player.getId());

            SellPointItem sellPointItem = new SellPointItem(item, player.getAccount().getId(), price);
            add(sellPointItem, false);
            player.send(SellPointPacketFormatter.simpleItemAddMessage(sellPointItem));
            player.send(SellPointPacketFormatter.sellItemsMessage(itemsByPlayer(player)));
            player.update();
        }
    }

    public long getAveragePrice(short template) {
        AtomicLong totalPrice = new AtomicLong(0);
        AtomicInteger totalCoefficient = new AtomicInteger(0);

        all().stream().filter(item -> item.getItem().getTemplate().getId() == template).forEach(goodItem -> {
            totalPrice.addAndGet(goodItem.getPrice());
            totalCoefficient.addAndGet(goodItem.getItem().getQuantity());
        });

        return totalCoefficient.get() == 0 ? 0 : totalPrice.get() / totalCoefficient.get();
    }

    public synchronized boolean buyItem(int line, byte amount, long price, Player buyer) {
        if (buyer.getInventory().getKamas() < price)
            return false;

        SellPointLine sellPointLine = this.getLine(line);
        SellPointItem toBuy = sellPointLine.getSellPointItem(amount, price);

        if (toBuy == null) {
            buyer.send(MessageFormatter.itemAlreadyPurchased());
            return false;
        }

        buyer.getInventory().addKamas(price * -1);

        AccountRepository accountRepository = buyer.getAccount().getClient().getAccountRepository();

        Account account = accountRepository.get(toBuy.getOwner());
        if (account != null) {
            account.getClient().send(MessageFormatter.personalItemPurchasedMessage(toBuy.getItem().getTemplate().getId(), price));
            account.getBank().setKamas(account.getBank().getKamas() + price);
        } else {
            (account = accountRepository.find(toBuy.getOwner())).getBank().setKamas(account.getBank().getKamas() + price);
            //todo : account notification
        }
        accountRepository.updateBank(account.getBank());

        buyer.send(PlayerPacketFormatter.asMessage(buyer));

        toBuy.getItem().setPosition(ItemPosition.NotEquipped);

        Item same = buyer.getInventory().addItem(toBuy.getItem(), false);

        if (same == null) {
            buyer.send(ItemPacketFormatter.addItemMessage(toBuy.getItem()));
            buyer.getAccount().getClient().getPlayerRepository().saveItem(toBuy.getItem(), buyer.getId());
        } else
            buyer.removeItem(toBuy.getItem());

        remove(toBuy);
        buyer.update();
        return true;
    }


}
