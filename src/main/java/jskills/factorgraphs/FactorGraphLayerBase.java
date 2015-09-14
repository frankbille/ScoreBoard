package jskills.factorgraphs;

import java.util.Collection;

public abstract class FactorGraphLayerBase<TValue> {

  public abstract Collection<Factor<TValue>> getUntypedFactors();
  public abstract void BuildLayer();

  public Schedule<TValue> createPriorSchedule()
  {
      return null;
  }

  public Schedule<TValue> createPosteriorSchedule()
  {
      return null;
  }

  // HACK

  public abstract void SetRawInputVariablesGroups(Object value);
  public abstract Object GetRawOutputVariablesGroups();
}
