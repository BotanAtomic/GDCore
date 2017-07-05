package org.graviton.game.job.craft;

import lombok.Data;
import org.graviton.xml.XMLElement;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Botan on 04/07/17. 16:07
 */
@Data
public final class CraftData {
    private final Map<Short, Short> ingredients;
    private final short result;

    public CraftData(XMLElement element) {
        this.ingredients = getIngredients(element.getAttribute("data").toString());
        this.result = element.getAttribute("item").toShort();
    }

    private Map<Short, Short> getIngredients(String data) {
        Map<Short, Short> ingredients = new HashMap<>();
        Stream.of(data.split(";")).forEach(craft -> ingredients.put(Short.parseShort(craft.split("\\*")[0]), Short.parseShort(craft.split("\\*")[1])));
        return ingredients;
    }

    public boolean check(Map<Short, Short> ingredients) {
        return this.ingredients.equals(ingredients);
    }
}
