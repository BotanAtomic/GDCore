package org.graviton.game.creature.npc;

import lombok.Data;
import org.jooq.Record;

import static org.graviton.database.jooq.game.tables.NpcTemplate.NPC_TEMPLATE;

/**
 * Created by kurdistan on 27/11/16.
 */
@Data
public class NpcTemplate {
    private final int id;
    private final short skin, size, initialQuestion;
    private final byte sex, customArtWork, extraClip;
    private final int[] colors;
    private final String accessories, sales, exchanges;

    public NpcTemplate(Record record) {
        this.id = record.get(NPC_TEMPLATE.ID);
        this.skin = record.get(NPC_TEMPLATE.SKIN);
        this.size = record.get(NPC_TEMPLATE.SIZE);
        this.initialQuestion = record.get(NPC_TEMPLATE.INITIAL_QUESTION);
        this.sex = record.get(NPC_TEMPLATE.SEX);
        this.customArtWork = record.get(NPC_TEMPLATE.CUSTOMARTWORK);
        this.extraClip = record.get(NPC_TEMPLATE.EXTRACLIP);
        this.colors = new int[]{record.get(NPC_TEMPLATE.COLOR1), record.get(NPC_TEMPLATE.COLOR2), record.get(NPC_TEMPLATE.COLOR3)};
        this.accessories = record.get(NPC_TEMPLATE.ACCESSORIES);
        this.sales = record.get(NPC_TEMPLATE.SALES);
        this.exchanges = record.get(NPC_TEMPLATE.EXCHANGES);
    }

}
