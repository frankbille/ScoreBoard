package jskills.trueskill.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jskills.IPlayer;
import jskills.factorgraphs.KeyedVariable;
import jskills.factorgraphs.Schedule;
import jskills.factorgraphs.ScheduleStep;
import jskills.numerics.GaussianDistribution;
import jskills.numerics.MathUtils;
import jskills.trueskill.TrueSkillFactorGraph;
import jskills.trueskill.factors.GaussianLikelihoodFactor;

public class PlayerSkillsToPerformancesLayer extends
    TrueSkillFactorGraphLayer<KeyedVariable<IPlayer, GaussianDistribution>, 
                              GaussianLikelihoodFactor,
                              KeyedVariable<IPlayer, GaussianDistribution>>
{
    public PlayerSkillsToPerformancesLayer(TrueSkillFactorGraph parentGraph)
    {
        super(parentGraph);
    }

    @Override
    public void BuildLayer()
    {
        for(List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeam : getInputVariablesGroups())
        {
            List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeamPlayerPerformances = new ArrayList<KeyedVariable<IPlayer, GaussianDistribution>>();

            for(KeyedVariable<IPlayer, GaussianDistribution> playerSkillVariable : currentTeam)
            {
                KeyedVariable<IPlayer, GaussianDistribution> playerPerformance =
                    CreateOutputVariable(playerSkillVariable.getKey());
                AddLayerFactor(CreateLikelihood(playerSkillVariable, playerPerformance));
                currentTeamPlayerPerformances.add(playerPerformance);
            }

            addOutputVariableGroup(currentTeamPlayerPerformances);
        }
    }

    private GaussianLikelihoodFactor CreateLikelihood(KeyedVariable<IPlayer, GaussianDistribution> playerSkill,
                                                      KeyedVariable<IPlayer, GaussianDistribution> playerPerformance)
    {
        return new GaussianLikelihoodFactor(MathUtils.square(ParentFactorGraph.getGameInfo().getBeta()), playerPerformance, playerSkill);
    }

    private KeyedVariable<IPlayer, GaussianDistribution> CreateOutputVariable(IPlayer key)
    {
        return new KeyedVariable<IPlayer, GaussianDistribution>(key, GaussianDistribution.UNIFORM, "%s's performance", key);
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule()
    {
        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        for (GaussianLikelihoodFactor likelihood : getLocalFactors()) {
            schedules.add(new ScheduleStep<GaussianDistribution>("Skill to Perf step", likelihood, 0));
        }
        return ScheduleSequence(schedules,
            "All skill to performance sending");
    }

    @Override
    public Schedule<GaussianDistribution> createPosteriorSchedule()
    {
        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        for (GaussianLikelihoodFactor likelihood : getLocalFactors()) {
            schedules.add(new ScheduleStep<GaussianDistribution>("Skill to Perf step", likelihood, 1));
        }
        return ScheduleSequence(schedules,
            "All skill to performance sending");
    }
}