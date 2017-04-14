package org.graviton.game.job;


import org.graviton.utils.Utils;
import org.graviton.xml.XMLElement;

import java.util.List;

/**
 * Created by Botan on 02/04/2017. 22:23
 */
public class JobTemplate {
    private final short id;
    private final List<Integer> tools;

    public JobTemplate(XMLElement element) {
        this.id = element.getAttribute("id").toShort();
        this.tools = Utils.arraysToList(element.getElementByTagName("craft").toString(), ",",  true);
    }

}
