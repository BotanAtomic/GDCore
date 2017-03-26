package org.graviton.game.fight.bonus;

import org.graviton.game.alignment.type.AlignmentType;
import org.graviton.game.client.player.Player;
import org.graviton.game.fight.Fight;
import org.graviton.game.fight.Fighter;
import org.graviton.game.fight.team.FightTeam;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Botan on 12/03/2017. 20:31
 */
public class AggressionFightBonus extends FightBonus {

    public AggressionFightBonus(FightTeam looser, FightTeam winner) {
        super(null, null);

        double totalGradeWin = winner.stream().mapToDouble(target -> target.getAlignment().getGrade()).sum();
        double totalLevelWin = winner.stream().mapToDouble(Fighter::getLevel).sum();

        double totalGradeLoose = looser.stream().mapToDouble(target -> target.getAlignment().getGrade()).sum();
        double totalLevelLoose = looser.stream().mapToDouble(Fighter::getLevel).sum();


        if (totalLevelWin - totalLevelLoose > 15 * looser.size())
            return;

        short honor = (short) ((100 * ((totalGradeLoose * totalLevelLoose) / (totalGradeWin * totalLevelWin))) / winner.size());

        Stream.concat(looser.stream(), winner.stream()).forEach(target -> target.setFightBonus(new AggressionFightBonus(target, honor, winner.containsFighter(target))));
    }

    private AggressionFightBonus(Fighter fighter, short honor, boolean win) {
        super(null, null);

        if (fighter.getAlignment().getType() != AlignmentType.NEUTRE) {
            super.honor = win ? honor : (short) -honor;


            if (fighter.getAlignment().getHonor() + super.honor < 0) {
                super.honor -= (super.honor - fighter.getAlignment().getHonor());
            }

            if (win && fighter.getAlignment().getDishonor() > 0) {
                super.dishonor = -1;
                fighter.getAlignment().removeDishonor();
            }

            fighter.getAlignment().addHonor(super.honor);
        }
    }
}
