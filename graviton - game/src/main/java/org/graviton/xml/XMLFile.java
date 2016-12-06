package org.graviton.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Created by Botan on 06/12/2016. 20:22
 */
public class XMLFile {
    private final Document document;

    public XMLFile(Document document) {
        this.document = document;
    }

    public Collection<XMLElement> getElementsByTagName(String tag) {
        Collection<XMLElement> elements = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagName(tag);

        IntStream.range(0, nodeList.getLength()).forEach(i -> elements.add(new XMLElement((Element) nodeList.item(i))));
        return elements;
    }
}
