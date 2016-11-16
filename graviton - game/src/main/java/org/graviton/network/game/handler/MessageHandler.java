package org.graviton.network.game.handler;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.AbstractHandler;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.GameProtocol;
import org.graviton.utils.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Botan on 05/11/2016 : 01:01
 */
@Slf4j
public class MessageHandler {
    private static Map<Short, AbstractHandler> defaultHandlers = new Short2ObjectOpenHashMap<>(16, Hash.FAST_LOAD_FACTOR);

    private Short2ObjectOpenHashMap<AbstractHandler> handlers;
    private GameClient client;

    public MessageHandler(GameClient gameClient) {
        this.handlers = new Short2ObjectOpenHashMap<>(16, Hash.FAST_LOAD_FACTOR);
        this.handlers.putAll(defaultHandlers);
        handlers.defaultReturnValue((client, data, header) -> log.debug("[Session {}] unknown packet {}", client.getId(), (header + data)));
        this.client = gameClient;
    }

    private static void register(String header, AbstractHandler handler) {
        defaultHandlers.put(StringUtils.stringToShort(header), handler);
    }

    public static void initialize() {
        register("AT", ((client, data, header) -> client.applyTicket(Integer.parseInt(data))));

        register("AV", ((client, data, header) -> client.send(GameProtocol.requestRegionalVersionMessage())));

        register("Ag", ((client, data, header) -> client.setLanguage(data)));

        register("Af", ((client, data, header) -> client.send(GameProtocol.getQueuePositionMessage())));

        register("AL", ((client, data, header) -> client.send(client.getAccount().getPlayerPacket(true))));

        register("AP", ((client, data, header) -> client.send(GameProtocol.playerNameSuggestionSuccessMessage(StringUtils.randomPseudo()))));

        register("AA", ((client, data, header) -> client.createPlayer(data)));

        register("AD", ((client, data, header) -> client.deletePlayer(Integer.parseInt(data.split("\\|")[0]), data.split("\\|")[1])));

        register("AS", ((client, data, header) -> client.selectPlayer(Integer.parseInt(data))));

        register("GC", ((client, data, header) -> client.createGame()));

        register("GI", ((client, data, header) -> client.sendGameInformations()));

        defaultHandlers = Collections.unmodifiableMap(defaultHandlers);
        log.debug("{} packets loaded", defaultHandlers.size());
    }

    public void handle(String data) {
        String header = data.substring(0, 2);
        this.handlers.get(StringUtils.stringToShort(header)).apply(client, data.substring(2), header);
    }
}
