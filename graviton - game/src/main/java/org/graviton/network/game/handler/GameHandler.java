package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fighter;
import org.graviton.game.house.House;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.game.items.Item;
import org.graviton.game.items.common.ItemPosition;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Botan on 22/11/2016 : 22:10
 */
@Slf4j
public class GameHandler {
    private final GameClient client;
    private final InteractionManager interactionManager;

    private boolean endFight;

    public GameHandler(GameClient client) {
        this.client = client;
        this.interactionManager = client.getInteractionManager();
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'A':
                createAction(Short.parseShort(data.substring(0, 3)), data.substring(3));
                break;

            case 'C':
                createGame(client.getPlayer());
                break;

            case 'D':
                client.send(GamePacketFormatter.fightDetailsMessage(client.getPlayer().getGameMap().getFightFactory().get(Integer.parseInt(data))));
                break;

            case 'H':
                client.getPlayer().getFight().switchHelp(client.getPlayer());
                break;

            case 'I':
                sendGameInformation(client.getPlayer());
                break;

            case 'K':
                finishAction(data);
                break;

            case 'L':
                client.send(GamePacketFormatter.fightInformationMessage(client.getPlayer().getGameMap().getFightFactory().getFights().values()));
                break;

            case 'N':
                client.getPlayer().getFight().switchLocked(client.getPlayer());
                break;

            case 'P':
                if (client.getPlayer().getFight() == null)
                    client.getPlayer().switchAlignmentEnabled(data.charAt(0));
                else
                    client.getPlayer().getFight().switchGroup(client.getPlayer());
                break;

            case 'Q': // 'Q'
                quitFight(data);
                break;

            case 'R': // 'R'
                client.getPlayer().getFight().setReady(client.getPlayer(), data.charAt(0) == '1');
                break;

            case 'S': // 'S'
                client.getPlayer().getFight().switchSpectator(client.getPlayer());
                break;

            case 'f': // 'f'
                client.getPlayer().getTeam().send(FightPacketFormatter.showCellMessage(client.getPlayer().getId(), Short.parseShort(data)));
                break;

            case 'p': // 'p'
                client.getPlayer().getFight().changeFighterPlace(client.getPlayer(), Short.parseShort(data));
                break;

            case 't': // 't'
                client.getPlayer().getFight().getTurnList().getCurrent().end(false);
                break;

            default:
                log.error("not implemented game packet '{}'", subHeader);
        }
    }


    private void createGame(Player player) {
        client.send(GamePacketFormatter.gameCreationSuccessMessage());
        client.send(PlayerPacketFormatter.asMessage(player, client.getEntityFactory().getExperience(player.getLevel()), player.getAlignment(), player.getStatistics()));
        client.send(GamePacketFormatter.regenTimerMessage((short) 2000));

        if (!player.getJobs().isEmpty()) {
            player.getJobs().values().forEach(job -> {
                player.send(JobPacketFormatter.startJobMessage(job));
                player.send(JobPacketFormatter.statisticsJobMessage(job));

                Item item = player.getInventory().getByPosition(ItemPosition.Weapon);

                if (item != null && job.getJobTemplate().getTools().contains(item.getTemplate().getId()))
                    player.send(JobPacketFormatter.jobToolMessage(job.getJobTemplate().getId()));

            });
        }

        if (endFight) {
            player.getGameMap().enterAfterFight(player);
            endFight = false;
        } else if (player.getFight() == null)
            player.getMap().enter(player);

    }

    private void sendGameInformation(Player player) {
        if (player.getFight() != null) {
            client.send(GamePacketFormatter.mapLoadedSuccessfullyMessage());
            player.getFight().reconnect(player);
            return;
        }

        client.send(player.getGameMap().buildData());


        if (player.getGameMap().getHouses() != null) {
            client.sendFormat(HousePacketFormatter.loadMessage(player.getGameMap().getHouses().values(), client.getEntityFactory()), "#");

            Collection<House> personalHouse = player.getGameMap().getHouses().values().stream().filter(house -> house.getOwner() == client.getAccount().getId()).collect(Collectors.toList());
            if (!personalHouse.isEmpty())
                client.sendFormat(HousePacketFormatter.loadPersonalHouse(personalHouse, true), "#");
        }

        if (player.getGameMap().getMountPark() != null)
            client.send(MountPacketFormatter.showMountParkMessage(player.getGameMap().getMountPark(), client.getEntityFactory()));


        client.sendFormat(player.getGameMap().interactiveObjectData(), "#");
        client.send(GamePacketFormatter.fightCountMessage(((GameMap) player.getMap()).getFightFactory().getFightSize()));
        ((GameMap) player.getMap()).getFightFactory().getFights().values().forEach(fight -> fight.buildFlag().forEach(client::send));
    }

    private void createAction(short id, String arguments) {
        this.interactionManager.create(id, arguments);
    }

    private void finishAction(String data) {
        interactionManager.end(interactionManager.pollFirst(), data.charAt(0) == 'K', data.substring(1));
    }

    private void quitFight(String data) {
        Fighter fighter = data.isEmpty() ? client.getPlayer() : client.getPlayerRepository().get(Integer.parseInt(data));

        if (!client.getPlayer().getTeam().contains(fighter))
            return;

        fighter.getFight().quit(fighter);
    }

    public void setEndFight() {
        this.endFight = true;
    }
}
