package org.graviton.network.game.handler;

import lombok.extern.slf4j.Slf4j;
import org.graviton.game.client.player.Player;
import org.graviton.game.spell.SpellView;
import org.graviton.network.game.GameClient;
import org.graviton.network.game.protocol.PlayerPacketFormatter;
import org.graviton.network.game.protocol.SpellPacketFormatter;

/**
 * Created by Botan on 25/12/2016. 02:57
 */

@Slf4j
public class SpellHandler {
    private final GameClient client;

    public SpellHandler(GameClient client) {
        this.client = client;
    }

    public void handle(String data, char subHeader) {
        switch (subHeader) {
            case 'B':
                boostSpell(Short.parseShort(data));
                break;

            case 'M':
                moveSpell(data.split("\\|"));
                break;

            default:
                log.error("not implemented spell packet '{}'", subHeader);
        }
    }

    private void boostSpell(short spell) {
        Player player = client.getPlayer();
        SpellView spellView = client.getPlayer().getSpellView(spell);

        byte cost = spellView.getSpell().getLevel();

        if (player.getStatistics().getSpellPoints() >= cost) {
            player.getStatistics().setSpellPoints((short) (player.getStatistics().getSpellPoints() - cost));
            spellView.setSpell(spellView.getSpell().next());
            client.send(PlayerPacketFormatter.asMessage(player, client.getEntityFactory().getExperience(player.getLevel()), player.getAlignment(), player.getStatistics()));
            client.send(SpellPacketFormatter.boostSpellSuccessMessage(spell, spellView.getSpell().getLevel()));
            client.getEntityFactory().getPlayerRepository().saveSpellView(spellView, client.getPlayer());
        } else
            client.send(SpellPacketFormatter.boostSpellErrorMessage());
    }

    private void moveSpell(String[] data) {
        byte position = Byte.parseByte(data[1]);

        SpellView spellView;

        if ((spellView = client.getPlayer().getSpellView(position)) != null) {
            spellView.setPosition((byte) -1);
            client.getEntityFactory().getPlayerRepository().saveSpellView(spellView, client.getPlayer());
        }

        spellView = client.getPlayer().getSpellView(Short.parseShort(data[0]));
        spellView.setPosition(Byte.parseByte(data[1]));

        client.getEntityFactory().getPlayerRepository().saveSpellView(spellView, client.getPlayer());
    }

}
