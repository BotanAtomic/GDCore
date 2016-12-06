package org.graviton.game.experience;

import lombok.Data;
import org.graviton.xml.XMLElement;

/**
 * Created by Botan on 11/11/2016 : 22:09
 */

@Data
public class Experience {
    private short alignment;
    private int mount, job;
    private long player, guild;

    private Experience next;

    public Experience(XMLElement element) {
        this.player = element.getAttribute("player").toLong();
        this.guild = element.getAttribute("guild").toLong();
        this.mount = element.getAttribute("mount").toInt();
        this.job = element.getAttribute("job").toInt();
        this.alignment = element.getAttribute("alignment").toShort();
    }

}
