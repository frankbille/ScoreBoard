package jskills;

import java.util.Collection;
import java.util.Map;

import jskills.GameInfo;
import jskills.IPlayer;
import jskills.ITeam;
import jskills.Rating;
import jskills.SkillCalculator;
import jskills.trueskill.FactorGraphTrueSkillCalculator;

/**
 * Calculates a TrueSkill rating using {@link FactorGraphTrueSkillCalculator}.
 */
public class TrueSkillCalculator {

    /** Static usage only **/ private TrueSkillCalculator() {}
    // Keep a singleton around
    private static final SkillCalculator _Calculator = new FactorGraphTrueSkillCalculator();

    /**
     * Calculates new ratings based on the prior ratings and team ranks.
     * @param gameInfo Parameters for the game.
     * @param teams A mapping of team players and their ratings.
     * @param teamRanks The ranks of the teams where 1 is first place. For a tie, repeat the number (e.g. 1, 2, 2)
     * @returns All the players and their new ratings.
     */
    public static Map<IPlayer, Rating> calculateNewRatings(GameInfo gameInfo,
            Collection<ITeam> teams, int... teamRanks) {
        // Just punt the work to the full implementation
        return _Calculator.calculateNewRatings(gameInfo, teams, teamRanks);
    }

    /**
     * Calculates the match quality as the likelihood of all teams drawing.
     * @param gameInfo Parameters for the game.
     * @param teams A mapping of team players and their ratings.
     * @returns The match quality as a percentage (between 0.0 and 1.0).
     */
    public static double calculateMatchQuality(GameInfo gameInfo,
                                                        Collection<ITeam> teams) {
        // Just punt the work to the full implementation
        return _Calculator.calculateMatchQuality(gameInfo, teams);
    }
}