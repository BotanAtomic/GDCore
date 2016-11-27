package org.graviton.game.look;

import lombok.Data;
import org.graviton.game.look.enums.OrientationEnum;

/**
 * Created by Botan on 11/11/2016 : 21:35
 */
@Data
public abstract class AbstractLook {
    private int[] colors;
    private short skin;
    private OrientationEnum orientation;

    public AbstractLook(int[] colors, short skin, byte orientation) {
        this.colors = colors;
        this.skin = skin;
        this.orientation = OrientationEnum.valueOf(orientation);
    }

}
