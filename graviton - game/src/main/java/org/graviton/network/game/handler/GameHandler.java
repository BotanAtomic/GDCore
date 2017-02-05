package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fighter;
import org.graviton.game.interaction.InteractionManager;
import org.graviton.game.maps.GameMap;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.FightPacketFormatter;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 22/11/2016 : 22:10
 */
@Slf4j
public class GameHandler {

    private final GameClient client;
    private final InteractionManager interactionManager;

    private boolean endFight = false;

    public GameHandler(GameClient client) {
        this.client = client;
        this.interactionManager = client.getInteractionManager();
    }

    public void handle(String data, byte subHeader) { // 'G'
        switch (subHeader) {
            case 65: // 'A'
                createAction(Short.parseShort(data.substring(0, 3)), data.substring(3));
                break;

            case 67: // 'C'
                createGame(client.getPlayer());
                break;

            case 68: // 'D'
                client.send(GamePacketFormatter.fightDetailsMessage(client.getPlayer().getGameMap().getFightFactory().get(Integer.parseInt(data))));
                break;

            case 72: // 'H'
                client.getPlayer().getFight().switchHelp(client.getPlayer());
                break;

            case 73: // 'I'
                sendGameInformation(client.getPlayer());
                break;

            case 75: // 'K'
                finishAction(data);
                break;

            case 76: // 'L'
                client.send(GamePacketFormatter.fightInformationMessage(client.getPlayer().getGameMap().getFightFactory().getFights().values()));
                break;

            case 78: // 'N'
                client.getPlayer().getFight().switchLocked(client.getPlayer());
                break;

            case 80: // 'P'
                client.getPlayer().getFight().switchGroup(client.getPlayer());
                break;

            case 81: // 'Q'
                quitFight(data);
                break;

            case 82: // 'R'
                client.getPlayer().getFight().setReady(client.getPlayer(), data.charAt(0) == '1');
                break;

            case 83: // 'S'
                client.getPlayer().getFight().switchSpectator(client.getPlayer());
                break;

            case 102: // 'f'
                client.getPlayer().getTeam().send(FightPacketFormatter.showCellMessage(client.getPlayer().getId(), Short.parseShort(data)));
                break;

            case 112: // 'p'
                client.getPlayer().getFight().changeFighterPlace(client.getPlayer(), Short.parseShort(data));
                break;

            case 116: // 't'
                client.getPlayer().getFight().getTurnList().getCurrent().end();
                break;

            default:
                log.error("not implemented game packet '{}'", (char) subHeader);
        }

    }


    private void createGame(Player player) {
        client.send(GamePacketFormatter.gameCreationSuccessMessage());
        client.send(PlayerPacketFormatter.asMessage(player, client.getEntityFactory().getExperience(player.getLevel()), player.getAlignment(), player.getStatistics()));
        client.send(GamePacketFormatter.regenTimerMessage((short) 2000));


        if (!endFight) {
            player.getMap().enter(player);
        } else {
            ((GameMap) player.getMap()).enterAfterFight(player);
            endFight = false;
        }
    }

    private void sendGameInformation(Player player) {
        client.send(player.getMap().buildData());
        client.send(GamePacketFormatter.fightCountMessage(((GameMap) player.getMap()).getFightFactory().getFightSize()));
        ((GameMap) player.getMap()).getFightFactory().getFights().values().forEach(fight -> fight.buildFlag().forEach(client::send));
    }

    private void createAction(short id, String arguments) {
        this.interactionManager.create(id, arguments);
    }

    private void finishAction(String data) {
        interactionManager.end(interactionManager.pollLast(), data.charAt(0) == 'K', data.substring(1));
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
