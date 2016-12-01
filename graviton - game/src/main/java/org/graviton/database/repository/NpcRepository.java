package org.graviton.database.repository;

import org.graviton.game.creature.npc.NpcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kurdistan on 27/11/16.
 */
public class NpcRepository {

    private final Map<Integer, NpcTemplate> templates = new ConcurrentHashMap<>();

    public void initialize() {

    }

}
