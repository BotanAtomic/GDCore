package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.constant.Dofus;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.merchant.Merchant;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.exchange.Exchange;
import org.graviton.game.exchange.type.*;
import org.graviton.game.sellpoint.SellPoint;
import org.graviton.game.items.Item;
import org.graviton.game.sellpoint.SellPointItem;
import org.graviton.game.sellpoint.SellPointLine;
import org.graviton.game.sellpoint.SellPointTemplate;
import org.graviton.lang.LanguageSentence;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.*;

import java.util.List;

/**
 * Created by Botan on 11/02/2017. 14:25
 */

@Slf4j
public class ExchangeHandler {
    private final GameClient client;

    public ExchangeHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'A':
                client.getPlayer().getExchange().accept();
                break;

            case 'B':
                client.getPlayer().getExchange().buy(Integer.parseInt(data.split("\\|")[0]), Short.parseShort(data.split("\\|")[1]));
                break;

            case 'H':
                bigStore(data);
                break;

            case 'K':
                client.getPlayer().getExchange().toggle(client.getPlayer().getId());
                break;

            case 'M':
                doExchangeAction(data);
                break;

            case 'Q':
                activeMerchantMode();
                break;

            case 'L':
                ((BreakerExchange) client.getPlayer().getExchange()).setLastIngredients();
                break;

            case 'R':
                request(data);
                break;

            case 'S':
                client.getPlayer().getExchange().sell(Integer.parseInt(data.split("\\|")[0]), Short.parseShort(data.split("\\|")[1]));
                break;

            case 'V':
                client.getPlayer().getExchange().cancel();
                break;

            case 'q':
                requestMerchantMode();
                break;

            default:
                log.error("not implemented exchange packet '{}'", subHeader);

        }
    }

    private void request(String packet) {
        String[] data = packet.split("\\|");
        SellPoint sellPoint = client.getPlayer().getGameMap().getSellPoint();

        switch (Byte.parseByte(data[0])) {
            case 0: //npc buy
                Npc npc = (Npc) client.getPlayer().getGameMap().getCreature(Integer.parseInt(data[1]));
                client.send(NpcPacketFormatter.buyRequestMessage(npc.getId()));
                client.send(NpcPacketFormatter.itemListMessage(npc.getTemplate().getItems(client.getEntityFactory())));
                client.getPlayer().setExchange(new NpcExchange(npc, client.getPlayer()));
                break;
            case 1: //player
                requestPlayerExchange(Integer.parseInt(data[1]));
                break;
            case 2: //npc exchange
                Npc npcExchanger = (Npc) client.getPlayer().getGameMap().getCreature(Integer.parseInt(data[1]));
                client.send(ExchangePacketFormatter.startMessage((byte) 2));
                new NpcItemExchange(client.getPlayer(), npcExchanger);
                break;
            case 4://merchant
                Merchant merchant = (Merchant) client.getPlayer().getGameMap().getCreature(Integer.parseInt(data[1]));

                if (merchant != null) {
                    if (merchant.isBusy()) {
                        client.send(MessageFormatter.customStaticMessage(client.getAccount().getClient().getLanguage().getSentence(LanguageSentence.MERCHANT_BUSY)));
                        return;
                    }
                    client.send(ExchangePacketFormatter.startMerchantMessage((byte) 4, merchant.getId()));
                    client.send(ExchangePacketFormatter.personalStoreMessage(merchant.getStore()));
                    client.getPlayer().setExchange(new MerchantExchange(merchant, client.getPlayer()));
                }
                break;
            case 6: //personal store
                client.send(ExchangePacketFormatter.startMessage((byte) 6));
                client.send(ExchangePacketFormatter.personalStoreMessage(client.getPlayer().getStore()));
                client.getPlayer().setExchange(new MyStoreExchange(client.getPlayer()));
                break;

            case 10: //sell point (sell)
                if (client.getPlayer().getExchange() != null)
                    client.getPlayer().getExchange().cancel();

                if (client.getPlayer().getAlignment().getDishonor() >= 5) {
                    client.send(MessageFormatter.notPermittedDishonorMessage());
                    return;
                }

                if (sellPoint != null) {
                    client.send(SellPointPacketFormatter.startMessage(sellPoint, true));
                    client.getInteractionManager().setSellPointInteraction(sellPoint);
                    client.getPlayer().setExchange(new SellPointExchange(client.getPlayer(), sellPoint));

                    List<SellPointItem> items = sellPoint.itemsByPlayer(client.getPlayer());

                    client.send(SellPointPacketFormatter.sellItemsMessage(items));
                }

                break;

            case 11: //sell point (buy)
                if (client.getPlayer().getExchange() != null)
                    client.getPlayer().getExchange().cancel();

                if (client.getPlayer().getAlignment().getDishonor() >= 5) {
                    client.send(MessageFormatter.notPermittedDishonorMessage());
                    return;
                }

                if (sellPoint != null) {
                    client.send(SellPointPacketFormatter.startMessage(sellPoint, false));
                    client.getInteractionManager().setSellPointInteraction(sellPoint);
                    client.getPlayer().setExchange(new CancelableExchange(client.getPlayer()));
                }

                break;

        }
    }

    private void requestPlayerExchange(int targetId) {
        Player target = client.getEntityFactory().getPlayerRepository().find(targetId);
        if (target.getMap().getId() == client.getPlayer().getMap().getId() && !target.isBusy()) {
            target.send(ExchangePacketFormatter.requestMessage(client.getPlayer().getId(), targetId, (byte) 1));
            client.send(ExchangePacketFormatter.requestMessage(client.getPlayer().getId(), targetId, (byte) 1));
            new PlayerExchange(client.getPlayer(), target);
        } else
            client.send(ExchangePacketFormatter.requestErrorMessage());
    }

    private void doExchangeAction(String packet) {
        Exchange exchange = client.getPlayer().getExchange();

        String[] information = packet.contains("|") ? packet.substring(2).split("\\|") : new String[2];
        switch (packet.charAt(0)) {
            case 'O':
                if (packet.charAt(1) == '+') {
                    int id = Integer.parseInt(information[0]);
                    short quantity = Short.parseShort(information[1]);
                    short quantityInExchange = exchange.getItemQuantity(client.getPlayer().getId(), id);

                    Item item = client.getPlayer().getInventory().get(id);

                    if (exchange instanceof MyStoreExchange && quantity == 0 && item == null) {
                        client.getPlayer().getStore().changePrice(id, Long.parseLong(information[2]));
                        return;
                    }

                    if (item == null || (quantity <= 0))
                        return;

                    if (quantity > item.getQuantity() - quantityInExchange)
                        quantity = (short) (item.getQuantity() - quantityInExchange);

                    if (exchange instanceof SellPointExchange)
                        client.getInteractionManager().getSellPointInteraction().addItem(client.getPlayer(), id, (byte) quantity, Long.parseLong(information[2]));
                    else if (exchange instanceof MyStoreExchange)
                        client.getPlayer().getStore().add(item, quantity, Long.parseLong(information[2]));
                    else
                        exchange.addItem(id, quantity, client.getPlayer().getId());
                } else
                    exchange.removeItem(Integer.parseInt(information[0]), Short.parseShort(information[1]), client.getPlayer().getId());

                break;
            case 'G':// Kamas
                exchange.editKamas(Long.parseLong(packet.substring(1)), client.getPlayer().getId());
                break;
            case 'R':// Repeat (for job only)
                exchange.toggle(-Integer.parseInt(packet.substring(1)));
                break;
            case 'r': //stop craft
                ((BreakerExchange) exchange).stopCraft();
                break;
        }
    }

    private void requestMerchantMode() {
        Player player = client.getPlayer();

        if (player.getStore().isEmpty()) {
            player.send(MessageFormatter.noStoreItemMessage());
            return;
        }

        player.send(ExchangePacketFormatter.merchantMessage(player.getStore().getTax()));
    }

    private void activeMerchantMode() {
        Player player = client.getPlayer();

        if (player.getGameMap().restrictMerchant()) {
            player.send(MessageFormatter.noMerchantPermitMessage());
            return;
        }

        if (player.getGameMap().merchantCount() > Dofus.MAX_STORE_PER_MAP) {
            player.send(MessageFormatter.noMerchantPlaceAvailableMessage(Dofus.MAX_STORE_PER_MAP));
            return;
        }

        if (player.getStore().getTax() > player.getInventory().getKamas()) {
            player.send(MessageFormatter.merchantTaxErrorMessage());
            return;
        } else player.getInventory().addKamas(-player.getStore().getTax());


        player.getEntityFactory().getPlayerRepository().addMerchant(player);
        player.getAccount().getClient().disconnect();
        player.getGameMap().enter(new Merchant(player.getStore()));
    }

    private void bigStore(String data) {

        switch (data.charAt(0)) {
            case 'B': {
                String[] information = data.substring(1).split("\\|");
                SellPoint sellPoint = client.getInteractionManager().getSellPointInteraction();

                int line = Integer.parseInt(information[0]);
                byte amount = Byte.parseByte(information[1]);
                SellPointLine sellPointLine = sellPoint.getLine(line);

                if (sellPointLine == null) {
                    client.send(MessageFormatter.itemAlreadyPurchased());
                    return;
                }

                SellPointItem sellPointItem = sellPointLine.getSellPointItem(amount, Long.parseLong(information[2]));

                if (sellPointItem == null) {
                    client.send(MessageFormatter.itemAlreadyPurchased());
                    return;
                }

                int owner = sellPointItem.getOwner();


                if (owner == client.getAccount().getId()) {
                    client.send(MessageFormatter.customStaticMessage(client.getLanguage().getSentence(LanguageSentence.BUY_OUR)));
                    return;
                }

                if (sellPoint.buyItem(line, amount, Long.parseLong(information[2]), client.getPlayer())) {
                    byte category = sellPointItem.getItem().getTemplate().getType().getValue();
                    short template = sellPointItem.getItem().getTemplate().getId();
                    client.send(SellPointPacketFormatter.categoryMessage(category, sellPoint.categoryTemplates(category)));

                    client.send(SellPointPacketFormatter.removeLineMessage(line));

                    if (sellPoint.getLine(line) != null && !sellPoint.getLine(line).isEmpty())
                        client.send(SellPointPacketFormatter.linesMessage(sellPoint.getCategories().get(category).getTemplate(template), template));


                    client.send(PlayerPacketFormatter.podsMessage(client.getPlayer().getStatistics().refreshPods()));
                    client.send(MessageFormatter.lotPurchasedMessage());
                } else
                    client.send(MessageFormatter.lotNotPurchasedMessage());

                break;
            }

            case 'T': {
                byte category = Byte.parseByte(data.substring(1));
                client.send(SellPointPacketFormatter.categoryMessage(category, client.getInteractionManager().getSellPointInteraction().categoryTemplates(category)));
                break;
            }
            case 'P': {
                short template = Short.parseShort(data.substring(1));
                client.send(SellPointPacketFormatter.averagePriceMessage(template, client.getInteractionManager().getSellPointInteraction().getAveragePrice(template)));
                break;
            }
            case 'l':
                short template = Short.parseShort(data.substring(1));
                byte category = client.getEntityFactory().getItemTemplate(template).getType().getValue();
                SellPointTemplate sellPointTemplate = client.getInteractionManager().getSellPointInteraction().getCategories().get(category).getTemplate(template);
                if (sellPointTemplate != null && !sellPointTemplate.getLines().isEmpty())
                    client.send(SellPointPacketFormatter.linesMessage(sellPointTemplate, template));
                break;
        }
    }

}
