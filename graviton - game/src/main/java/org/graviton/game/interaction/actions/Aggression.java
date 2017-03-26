package org.graviton.game.interaction.actions;

import org.graviton.game.alignment.type.AlignmentType;
import org.graviton.game.client.player.Player;
import org.graviton.game.interaction.AbstractGameAction;
import org.graviton.network.game.protocol.GamePacketFormatter;
import org.graviton.network.game.protocol.PlayerPacketFormatter;

/**
 * Created by Botan on 12/03/2017. 17:38
 */
public class Aggression implements AbstractGameAction {
    private final Player aggressor;
    private final Player attacked;

    public Aggression(Player aggressor, Player attacked) {
        this.aggressor = aggressor;
        this.attacked = attacked;
    }

    @Override
    public boolean begin() {
        if (this.aggressor == null || this.attacked == null || this.attacked.getMap().getId() != this.aggressor.getMap().getId())
            return false;

        if (!aggressor.getGameMap().getPlaces().isEmpty() && aggressor.getGameMap().getPlaces().length() > 4) {

            if (attacked.getAlignment().getType() == AlignmentType.NEUTRE) {
                aggressor.getAlignment().addDishonor();
                aggressor.send(PlayerPacketFormatter.dishonorMessage());
            }

            if(!aggressor.getAlignment().isEnabled())
                aggressor.getAlignment().setEnabled(true);

            aggressor.getGameMap().send(GamePacketFormatter.aggressionMessage(aggressor.getId(), attacked.getId()));
            aggressor.getGameMap().getFightFactory().newAggressionFight(aggressor, attacked);
        }

        return false;
    }

    @Override
    public void cancel(String data) {

    }

    @Override
    public void finish(String data) {

    }
}
