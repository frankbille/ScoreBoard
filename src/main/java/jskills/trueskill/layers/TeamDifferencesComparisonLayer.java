package jskills.trueskill.layers;

import jskills.GameInfo;
import jskills.factorgraphs.DefaultVariable;
import jskills.factorgraphs.Variable;
import jskills.numerics.GaussianDistribution;
import jskills.trueskill.DrawMargin;
import jskills.trueskill.TrueSkillFactorGraph;
import jskills.trueskill.factors.GaussianFactor;
import jskills.trueskill.factors.GaussianGreaterThanFactor;
import jskills.trueskill.factors.GaussianWithinFactor;

public class TeamDifferencesComparisonLayer extends
    TrueSkillFactorGraphLayer<Variable<GaussianDistribution>, GaussianFactor, DefaultVariable<GaussianDistribution>>
{
    private final double _Epsilon;
    private final int[] _TeamRanks;

    public TeamDifferencesComparisonLayer(TrueSkillFactorGraph parentGraph, int[] teamRanks)
    {
        super(parentGraph);
        _TeamRanks = teamRanks;
        GameInfo gameInfo = ParentFactorGraph.getGameInfo();
        _Epsilon = DrawMargin.GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(), gameInfo.getBeta());
    }

    @Override
    public void BuildLayer()
    {
        for (int i = 0; i < getInputVariablesGroups().size(); i++)
        {
            boolean isDraw = (_TeamRanks[i] == _TeamRanks[i + 1]);
            Variable<GaussianDistribution> teamDifference = getInputVariablesGroups().get(i).get(0);

            GaussianFactor factor =
                isDraw
                    ? (GaussianFactor) new GaussianWithinFactor(_Epsilon, teamDifference)
                    : new GaussianGreaterThanFactor(_Epsilon, teamDifference);

            AddLayerFactor(factor);
        }
    }
}