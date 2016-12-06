package org.graviton.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Created by Botan on 06/12/2016. 20:29
 */
public class XMLElement {
    private final Element element;

    public XMLElement(Element element) {
        this.element = element;
    }

    public Attribute getAttribute(String attribute) {
        return new Attribute(element.getAttribute(attribute));
    }

    public Attribute getElementByTagName(String tag) {
        return new Attribute(element.getElementsByTagName(tag).item(0).getTextContent());
    }

    public Attribute getElementByTagName(String tag, String subTag) {
        return new Attribute(((Element) element.getElementsByTagName(tag).item(0)).getAttribute(subTag));
    }

    public Collection<XMLElement> getElementsByTagName(String tag) {
        Collection<XMLElement> elements = new ArrayList<>();

        NodeList nodeList = element.getElementsByTagName(tag);

        IntStream.range(0, nodeList.getLength()).forEach(i -> elements.add(new XMLElement((Element) nodeList.item(i))));
        return elements;
    }

}
