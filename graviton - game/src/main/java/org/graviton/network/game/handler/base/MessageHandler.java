package org.graviton.network.game.handler.base;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.handler.*;

/**
 * Created by Botan on 05/11/2016 : 01:01
 */
@Slf4j
@Data
public class MessageHandler {
    private final GameClient client;
    private final AccountHandler accountHandler;
    private final GameHandler gameHandler;
    private final BasicHandler basicHandler;
    private final ItemHandler itemHandler;
    private final EnvironmentHandler environmentHandler;
    private final DialogHandler dialogHandler;
    private final SpellHandler spellHandler;
    private final PartyHandler partyHandler;
    private final ExchangeHandler exchangeHandler;
    private final GuildHandler guildHandler;
    private final ConquestHandler conquestHandler;
    private final HouseHandler houseHandler;
    private final CodeHandler codeHandler;
    private final WaypointHandler waypointHandler;
    private final QuestHandler questHandler;

    public MessageHandler(GameClient gameClient) {
        this.client = gameClient;
        this.accountHandler = new AccountHandler(gameClient);
        this.gameHandler = new GameHandler(gameClient);
        this.basicHandler = new BasicHandler(gameClient);
        this.itemHandler = new ItemHandler(gameClient);
        this.environmentHandler = new EnvironmentHandler(gameClient);
        this.dialogHandler = new DialogHandler(gameClient);
        this.spellHandler = new SpellHandler(gameClient);
        this.partyHandler = new PartyHandler(gameClient);
        this.exchangeHandler = new ExchangeHandler(gameClient);
        this.guildHandler = new GuildHandler(gameClient);
        this.conquestHandler = new ConquestHandler(gameClient);
        this.houseHandler = new HouseHandler(gameClient);
        this.codeHandler = new CodeHandler(gameClient);
        this.waypointHandler = new WaypointHandler(gameClient);
        this.questHandler = new QuestHandler(gameClient);
    }

    public void handle(String data) {
        switch (data.charAt(0)) {
            case 'A': //Account
                this.accountHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'B': //Basic
                this.basicHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'C': //Conquest
                this.conquestHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'D': //Dialog
                this.dialogHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'E': //Exchange
                this.exchangeHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'f': //Fight
            case 'G': //Game
                this.gameHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'K': //(C/K)ode
                this.codeHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'O': //Item
                this.itemHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'I': //Party
                this.partyHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'Q': //Quest
                this.questHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'S': //Spell
                this.spellHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'W': //Waypoint
                this.waypointHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'e': //Environment
                this.environmentHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'g': //Guild
                this.guildHandler.handle(data.substring(2), data.charAt(1));
                break;

            case 'h': //House
                this.houseHandler.handle(data.substring(2), data.charAt(1));
                break;

            default:
                log.error("not implemented packet {}", data);
        }

    }
}
