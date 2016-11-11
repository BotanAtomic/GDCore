package org.graviton.game.experience;

import lombok.Data;
import org.w3c.dom.Element;

/**
 * Created by Botan on 11/11/2016 : 22:09
 */

@Data
public class Experience {
    private short alignement;
    private int mount, job;
    private long player, guild;

    private Experience next, previous;

    public Experience(Element element) {
        this.player = Long.parseLong(element.getAttribute("player"));
        this.guild = Long.parseLong(element.getAttribute("guild"));
        this.mount = Integer.parseInt(element.getAttribute("mount"));
        this.job = Integer.parseInt(element.getAttribute("job"));
        this.alignement = Short.parseShort(element.getAttribute("alignement"));
    }

}
