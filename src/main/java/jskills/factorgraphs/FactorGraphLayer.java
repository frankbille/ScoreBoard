package jskills.factorgraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FactorGraphLayer
<TParentFactorGraph extends FactorGraph<TParentFactorGraph>, 
TValue, 
TBaseVariable extends Variable<TValue>, 
TInputVariable extends Variable<TValue>, 
TFactor extends Factor<TValue>, 
TOutputVariable extends Variable<TValue>> 
    extends FactorGraphLayerBase<TValue> {

    private final List<TFactor> _LocalFactors = new ArrayList<TFactor>();
    private final List<List<TOutputVariable>> _OutputVariablesGroups = new ArrayList<List<TOutputVariable>>();
    private List<List<TInputVariable>> _InputVariablesGroups = new ArrayList<List<TInputVariable>>();

    protected FactorGraphLayer(TParentFactorGraph parentGraph)
    {
        ParentFactorGraph = parentGraph;
    }

    protected List<List<TInputVariable>> getInputVariablesGroups() {
        return _InputVariablesGroups;
    }

    // HACK

    public TParentFactorGraph ParentFactorGraph;
    public TParentFactorGraph getParentFactorGraph() { return ParentFactorGraph; }
    @SuppressWarnings("unused") // TODO remove if really unnecessary
    private void setParentFactorGraph( TParentFactorGraph parent ) { ParentFactorGraph = parent; }
    
    public List<List<TOutputVariable>> getOutputVariablesGroups(){
        return _OutputVariablesGroups;
    }
    
    public void addOutputVariableGroup(List<TOutputVariable> group) {
        _OutputVariablesGroups.add(group);
    }
    
    public void addOutputVariable(TOutputVariable var) {
        List<TOutputVariable> g = new ArrayList<TOutputVariable>(1); g.add(var);
        addOutputVariableGroup(g);
    }

    public List<TFactor> getLocalFactors() {
        return _LocalFactors;
    }

    @Override
    @SuppressWarnings("unchecked") // TODO there has to be a safer way to do this
    public Collection<Factor<TValue>> getUntypedFactors() {
        return (Collection<Factor<TValue>>) _LocalFactors;
    }

    @Override
    @SuppressWarnings("unchecked") // TODO there has to be a safer way to do this
    public void SetRawInputVariablesGroups(Object value)
    {
        List<List<TInputVariable>> newList = (List<List<TInputVariable>>)value;
        _InputVariablesGroups = newList;
    }

    @Override
    public Object GetRawOutputVariablesGroups()
    {
        return _OutputVariablesGroups;
    }

    protected Schedule<TValue> ScheduleSequence(
        Collection<Schedule<TValue>> itemsToSequence,
        String nameFormat,
        Object... args)
    {
        String formattedName = String.format(nameFormat, args);
        return new ScheduleSequence<TValue>(formattedName, itemsToSequence);
    }

    protected void AddLayerFactor(TFactor factor)
    {
        _LocalFactors.add(factor);
    }
}