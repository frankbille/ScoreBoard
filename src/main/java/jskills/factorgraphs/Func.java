package jskills.factorgraphs;

/**
 * A function that takes no parameters and returns a value of the type specified
 * by the TResult parameter.
 * 
 * @param <TResult>
 *            The return type of the function.
 */
public interface Func<TResult> {

    public TResult eval();
}
