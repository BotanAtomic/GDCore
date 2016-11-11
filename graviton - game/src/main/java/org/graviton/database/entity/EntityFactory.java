package org.graviton.database.entity;

import com.google.inject.Inject;
import org.graviton.api.Manageable;
import org.graviton.core.Program;
import org.graviton.game.experience.Experience;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Created by Botan on 11/11/2016 : 22:42
 */
public class EntityFactory implements Manageable {
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    private final Map<Short, Experience> experiences = new ConcurrentHashMap<>();

    @Inject
    public EntityFactory(Program program) {
        program.register(this);
    }

    private void loadExperiences() {
        Document document = get("experiences/experiences.xml");
        assert document != null;
        NodeList nodeList = document.getElementsByTagName("experience");

        IntStream.range(0, nodeList.getLength()).forEach(i -> {
            Element element = (Element) nodeList.item(i);
            this.experiences.put(Short.parseShort(element.getAttribute("level")), new Experience(element));
        });

        this.experiences.keySet().forEach(i -> {
            Experience experience = this.experiences.get(i);
            experience.setNext(this.experiences.get((short) (i + 1)));
            experience.setPrevious(this.experiences.get((short) (i - 1)));
        });
    }

    @Override
    public void start() {
        loadExperiences();
    }

    @Override
    public void stop() {

    }

    private Document get(String path) {
        try {
            return documentBuilderFactory.newDocumentBuilder().parse(new File("data/" + path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
