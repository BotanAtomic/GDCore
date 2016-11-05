package org.graviton.network.game.handler;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.graviton.api.AbstractHandler;
import org.graviton.network.game.GameClient;
import org.graviton.utils.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Botan on 05/11/2016 : 01:01
 */
@Slf4j
public class MessageHandler {

    private static Map<Short, AbstractHandler> defaultHandlers = new Short2ObjectOpenHashMap<>(16, Hash.FAST_LOAD_FACTOR);

    private Map<Short, AbstractHandler> handlers;
    private GameClient client;

    public MessageHandler(GameClient client) {
        this.handlers = new Short2ObjectOpenHashMap<>(16, Hash.FAST_LOAD_FACTOR);
        this.handlers.putAll(defaultHandlers);
        this.client = client;
    }

    private static void register(String header, AbstractHandler handler) {
        defaultHandlers.put(StringUtils.stringToShort(header), handler);
    }

    public static void initialize() {
        register("AT", ((client, data) -> {

        }));

        defaultHandlers = Collections.unmodifiableMap(defaultHandlers);
        log.debug("{} packets loaded", defaultHandlers.size());
    }

    public void handle(String data) {
        this.handlers.get(StringUtils.stringToShort(data.substring(0, 2))).apply(client, data.substring(2));
    }
}
