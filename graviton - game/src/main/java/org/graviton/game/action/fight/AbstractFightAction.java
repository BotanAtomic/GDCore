package org.graviton.game.action.fight;


import org.graviton.database.entity.EntityFactory;
import org.graviton.game.action.Action;
import org.graviton.game.client.player.Player;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 26/03/2017. 14:13
 */
public class AbstractFightAction {
    private final Action fightAction;
    private final String argument;

    public AbstractFightAction(XMLElement element, EntityFactory entityFactory) {
        this.fightAction = entityFactory.getActionRepository().create(element.getAttribute("action").toShort());
        this.argument = element.getAttribute("argument").toString();
    }

    public void apply(Player player) {
        fightAction.apply(player.getAccount().getClient(), argument);
    }

}
