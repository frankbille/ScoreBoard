package jskills.trueskill.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jskills.factorgraphs.Factor;
import jskills.factorgraphs.Schedule;
import jskills.factorgraphs.ScheduleLoop;
import jskills.factorgraphs.ScheduleSequence;
import jskills.factorgraphs.ScheduleStep;
import jskills.factorgraphs.Variable;
import jskills.numerics.GaussianDistribution;
import jskills.trueskill.TrueSkillFactorGraph;
import jskills.trueskill.factors.GaussianWeightedSumFactor;

// The whole purpose of this is to do a loop on the bottom
public class IteratedTeamDifferencesInnerLayer extends
    TrueSkillFactorGraphLayer<Variable<GaussianDistribution>, GaussianWeightedSumFactor, Variable<GaussianDistribution>>
{
    private final TeamDifferencesComparisonLayer _TeamDifferencesComparisonLayer;

    private final TeamPerformancesToTeamPerformanceDifferencesLayer
        _TeamPerformancesToTeamPerformanceDifferencesLayer;

    public IteratedTeamDifferencesInnerLayer(TrueSkillFactorGraph parentGraph,
                                             TeamPerformancesToTeamPerformanceDifferencesLayer
                                                 teamPerformancesToPerformanceDifferences,
                                             TeamDifferencesComparisonLayer teamDifferencesComparisonLayer)
    {
        super(parentGraph);
        _TeamPerformancesToTeamPerformanceDifferencesLayer = teamPerformancesToPerformanceDifferences;
        _TeamDifferencesComparisonLayer = teamDifferencesComparisonLayer;
    }

    @Override
    public Collection<Factor<GaussianDistribution>> getUntypedFactors() {
        Collection<Factor<GaussianDistribution>> factors = new ArrayList<Factor<GaussianDistribution>>() {
            private static final long serialVersionUID = 6370771040490033445L; {
           addAll(_TeamPerformancesToTeamPerformanceDifferencesLayer.getUntypedFactors());
           addAll(_TeamDifferencesComparisonLayer.getUntypedFactors());
        }};
        
        return factors;
    }

    @Override
    public void BuildLayer()
    {
        _TeamPerformancesToTeamPerformanceDifferencesLayer.SetRawInputVariablesGroups(getInputVariablesGroups());
        _TeamPerformancesToTeamPerformanceDifferencesLayer.BuildLayer();

        _TeamDifferencesComparisonLayer.SetRawInputVariablesGroups(
            _TeamPerformancesToTeamPerformanceDifferencesLayer.GetRawOutputVariablesGroups());
        _TeamDifferencesComparisonLayer.BuildLayer();
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule()
    {
        Schedule<GaussianDistribution> loop = null;

        switch (getInputVariablesGroups().size())
        {
            case 0:
            case 1:
                throw new IllegalArgumentException();
            case 2:
                loop = CreateTwoTeamInnerPriorLoopSchedule();
                break;
            default:
                loop = CreateMultipleTeamInnerPriorLoopSchedule();
                break;
        }

        // When dealing with differences, there are always (n-1) differences, so add in the 1
        int totalTeamDifferences = _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size();

        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        schedules.add(loop);
        schedules.add(new ScheduleStep<GaussianDistribution>(
                    "teamPerformanceToPerformanceDifferenceFactors[0] @ 1",
                    _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(0), 1));
        schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("teamPerformanceToPerformanceDifferenceFactors[teamTeamDifferences = %d - 1] @ 2",
                            totalTeamDifferences),
              _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(totalTeamDifferences - 1), 2));

        return new ScheduleSequence<GaussianDistribution>(
                "inner schedule", schedules);
    }

    private Schedule<GaussianDistribution> CreateTwoTeamInnerPriorLoopSchedule()
    {
        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        schedules.add(new ScheduleStep<GaussianDistribution>(
                "send team perf to perf differences",
                _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(0),
                0));
        schedules.add(new ScheduleStep<GaussianDistribution>(
                "send to greater than or within factor",
                _TeamDifferencesComparisonLayer.getLocalFactors().get(0),
                0));
        return ScheduleSequence(schedules,"loop of just two teams inner sequence");
    }

    private Schedule<GaussianDistribution> CreateMultipleTeamInnerPriorLoopSchedule()
    {
        int totalTeamDifferences = _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size();

        List<Schedule<GaussianDistribution>> forwardScheduleList = new ArrayList<Schedule<GaussianDistribution>>();

        for (int i = 0; i < totalTeamDifferences - 1; i++)
        {
            Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
            schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("team perf to perf diff %d",
                            i),
              _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(i), 0));
            schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("greater than or within result factor %d",
                            i),
              _TeamDifferencesComparisonLayer.getLocalFactors().get(i),
              0));
            schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("team perf to perf diff factors [%d], 2",
                            i),
              _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(i), 2));
            Schedule<GaussianDistribution> currentForwardSchedulePiece =
                ScheduleSequence(schedules, "current forward schedule piece %d", i);

            forwardScheduleList.add(currentForwardSchedulePiece);
        }

        ScheduleSequence<GaussianDistribution> forwardSchedule = new ScheduleSequence<GaussianDistribution>(
            "forward schedule",
            forwardScheduleList);

        List<Schedule<GaussianDistribution>> backwardScheduleList = new ArrayList<Schedule<GaussianDistribution>>();

        for (int i = 0; i < totalTeamDifferences - 1; i++)
        {
            Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
            schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 0",
                            i),
              _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(
                  totalTeamDifferences - 1 - i), 0));
            schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("greaterThanOrWithinResultFactors[totalTeamDifferences - 1 - %d] @ 0",
                            i),
              _TeamDifferencesComparisonLayer.getLocalFactors().get(totalTeamDifferences - 1 - i), 0));
            schedules.add(new ScheduleStep<GaussianDistribution>(
                    String.format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 1",
                            i),
              _TeamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(
                  totalTeamDifferences - 1 - i), 1));
            
            ScheduleSequence<GaussianDistribution> currentBackwardSchedulePiece = new ScheduleSequence<GaussianDistribution>(
                "current backward schedule piece", schedules);
            backwardScheduleList.add(currentBackwardSchedulePiece);
        }

        ScheduleSequence<GaussianDistribution> backwardSchedule = new ScheduleSequence<GaussianDistribution>(
            "backward schedule",
            backwardScheduleList);

        Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<Schedule<GaussianDistribution>>();
        schedules.add(forwardSchedule);
        schedules.add(backwardSchedule);
        ScheduleSequence<GaussianDistribution> forwardBackwardScheduleToLoop = new ScheduleSequence<GaussianDistribution>(
            "forward Backward Schedule To Loop", schedules);

        final double initialMaxDelta = 0.0001;

        ScheduleLoop<GaussianDistribution> loop = new ScheduleLoop<GaussianDistribution>(
            String.format("loop with max delta of %f",
                          initialMaxDelta),
            forwardBackwardScheduleToLoop,
            initialMaxDelta);

        return loop;
    }
}