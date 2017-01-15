package org.graviton.game.spell;

import lombok.Data;
import org.graviton.game.items.common.Bonus;
import org.graviton.game.spell.common.SpellEffects;
import org.graviton.game.spell.common.Target;
import org.graviton.game.spell.zone.Zone;
import org.graviton.game.spell.zone.ZoneType;
import org.graviton.game.spell.zone.type.CircleZone;
import org.graviton.game.spell.zone.type.CrossZone;
import org.graviton.game.spell.zone.type.LineZone;
import org.graviton.game.spell.zone.type.SingleCellZone;

/**
 * Created by Botan on 24/12/2016. 17:58
 */

@Data
public class SpellEffect {
    private final SpellEffects type;
    private short first, second, third;
    private short turns, chance;
    private Bonus dice;
    private Target target;
    private Zone zone;
    private Spell spell;

    public SpellEffect(int type) {
        this.type = SpellEffects.get(type);
    }

    void setCompiledZone(String zone) {
        switch (ZoneType.valueOf(zone.charAt(0))) {
            case CIRCLE:
                this.zone = new CircleZone(this, zone);
                break;
            case SINGLE_CELL:
                this.zone = new SingleCellZone(this, zone);
                break;
            case CROSS:
                this.zone = new CrossZone(this, zone);
                break;
            case LINE:
                this.zone = new LineZone(this, zone);
                break;
        }
    }

    public void setTarget(int target) {
        this.target = Target.get(target);
    }

    public short getSpellId() {
        return this.spell != null ? this.spell.getTemplate().getId() : 0;
    }

}
