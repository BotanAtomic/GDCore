package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.client.player.Player;
import org.graviton.game.creature.npc.Npc;
import org.graviton.game.creature.npc.NpcAnswer;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.NpcPacketFormatter;

/**
 * Created by Botan on 07/12/2016. 20:27
 */

@Slf4j
public class DialogHandler {
    private final GameClient client;
    private final EntityFactory entityFactory;
    private Player player;

    public DialogHandler(GameClient client) {
        this.client = client;
        this.entityFactory = client.getEntityFactory();
    }

    private Player getPlayer() {
        return this.player != null ? this.player : (player = client.getPlayer());
    }

    public void handle(String data, byte subHeader) { // 'D'
        switch (subHeader) {
            case 67: // 'C'
                createDialog(data);
                break;

            case 82: // 'R'
                answerDialog(data);
                break;

            case 86: // 'V'
                leaveDialog();
                break;

            default:
                log.error("not implemented dialog packet '{}'", (char) subHeader);

        }
    }

    public void createDialog(String data) {
        int id = Integer.parseInt(data);

        client.getInteractionManager().setInteractionWith(id);

        Npc npc = (Npc) getPlayer().getGameMap().getCreature(id);

        client.send(NpcPacketFormatter.createDialog(id));
        client.send(NpcPacketFormatter.questionMessage(entityFactory.getNpcQuestions().get(npc.getTemplate().getInitialQuestion(getPlayer().getGameMap().getId())).toString(player)));
    }

    public void createQuestion(String data) {
        if (data.equals("DV")) {
            leaveDialog();
            return;
        }

        client.send(NpcPacketFormatter.questionMessage(entityFactory.getNpcQuestions().get(Short.parseShort(data)).toString(player)));
    }

    private void answerDialog(String data) {
        NpcAnswer npcAnswer = entityFactory.getNpcAnswers().get(Short.parseShort(data.split("\\|")[1]));
        npcAnswer.getNpcAction().apply(client, npcAnswer.getData());
    }

    public void leaveDialog() {
        client.getInteractionManager().setInteractionWith(0);
        client.send(NpcPacketFormatter.quitMessage());
    }

}
