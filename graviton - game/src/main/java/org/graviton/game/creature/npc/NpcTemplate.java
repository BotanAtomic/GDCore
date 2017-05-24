package org.graviton.game.creature.npc;

import lombok.Data;
import org.graviton.database.entity.EntityFactory;
import org.graviton.game.creature.npc.exchange.ExchangeParser;
import org.graviton.game.items.template.ItemTemplate;
import org.graviton.game.look.NpcLook;
import org.graviton.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Botan on 27/11/16. 23:02
 */
@Data
public class NpcTemplate {
    private final int id;
    private final short size;
    private final byte sex;
    private final String sales, initialQuestion;
    private final NpcLook look;

    private ExchangeParser exchangeParser;

    private List<ItemTemplate> items;

    public NpcTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toInt();
        this.size = element.getElementByTagName("size").toShort();
        this.initialQuestion = element.getElementByTagName("initialQuestion").toString();
        this.sex = element.getElementByTagName("sex").toByte();
        this.sales = element.getElementByTagName("sales").toString();
        this.look = new NpcLook(element);

        String exchange = element.getElementByTagName("exchanges").toString();
        if (!exchange.isEmpty())
            this.exchangeParser = new ExchangeParser(exchange);
    }

    public short getInitialQuestion(int gameMap) {
        if (initialQuestion.contains(",")) {
            for (String question : initialQuestion.split("\\|"))
                if (question.split(",")[0].equals(Integer.toString(gameMap)))
                    return Short.parseShort(question.split(",")[1]);
        } else
            return Short.parseShort(initialQuestion);
        return (short) -1;
    }

    public short getSkin() {
        return this.look.getSkin();
    }

    public int[] getColors() {
        return this.look.getColors();
    }

    public String getAccessories() {
        return this.look.getAccessories();
    }

    public byte getExtraClip() {
        return this.look.getExtraClip();
    }

    public short getCustomArtWork() {
        return this.look.getCustomArtWork();
    }

    public List<ItemTemplate> getItems(EntityFactory entityFactory) {
        return (this.items == null ? (this.items = compileItems(entityFactory)) : this.items);
    }

    private List<ItemTemplate> compileItems(EntityFactory entityFactory) {
        List<ItemTemplate> itemTemplates = new ArrayList<>();
        for (String item : sales.split(",")) {
            if (!item.isEmpty())
                itemTemplates.add(entityFactory.getItemTemplate(Short.parseShort(item)));
        }
        return itemTemplates;
    }

    public ItemTemplate getItemTemplate(int id) {
        return this.items.stream().filter(template -> template.getId() == id).findAny().orElse(null);
    }

}
