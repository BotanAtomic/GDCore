package org.graviton.game.filter;

import lombok.Getter;
import org.graviton.game.client.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Botan on 27/12/2016. 15:11
 */
public class ConditionList {
    @Getter
    private final Set<Condition> conditions = new HashSet<>();

    public ConditionList(String data) {
        parse(data);
    }

    public boolean check(Player player) {
        if (conditions.isEmpty())
            return true;

        if (conditions.stream().filter(Condition::isOptional).count() > 0)
            if (conditions.stream().filter(Condition::isOptional).filter(condition -> condition.check(player)).count() == 0)
                return false;

        Stream<?> stream = conditions.stream().filter(condition -> !condition.isOptional()).filter(condition -> condition.check(player));

        return stream.count() == conditions.stream().filter(condition -> !condition.isOptional()).count();
    }


    public void parse(String data) {

        if (data.isEmpty())
            return;

        if (data.contains("&") && !data.contains("|"))
            this.conditions.addAll(parse(data, "&", false));
        else if (!data.contains("&") && data.contains("|"))
            this.conditions.addAll(parse(data, "\\|", true));
        else if (data.contains("&") && data.contains("|")) {
            for (String all : data.split("&"))
                if (all.contains("|"))
                    this.conditions.addAll(parse(all, "\\|", true));
                else
                    this.conditions.addAll(parse(all, "&", false));
        } else
            this.conditions.add(Condition.parse(data, false));

    }

    private Set<Condition> parse(String data, String regex, boolean optional) {
        Set<Condition> conditions = new HashSet<>();
        for (String notOptional : data.split(regex))
            conditions.add(Condition.parse(notOptional, optional));
        return conditions;
    }
}
