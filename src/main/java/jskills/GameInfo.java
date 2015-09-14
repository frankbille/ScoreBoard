package jskills;

/**
 * Parameters about the game for calculating the TrueSkill.
 */
public class GameInfo {

    private static final double defaultInitialMean = 25.0;
    private static final double defaultBeta = defaultInitialMean/6.0;
    private static final double defaultDrawProbability = 0.10;
    private static final double defaultDynamicsFactor = defaultInitialMean/300.0;
    private static final double defaultInitialStandardDeviation = defaultInitialMean/3.0;

    private double initialMean;
    private double initialStandardDeviation;
    private double beta;
    private double dynamicsFactor;
    private double drawProbability;

    public GameInfo(double initialMean, double initialStandardDeviation,
            double beta, double dynamicFactor, double drawProbability) {
        this.initialMean = initialMean;
        this.initialStandardDeviation = initialStandardDeviation;
        this.beta = beta;
        this.dynamicsFactor = dynamicFactor;
        this.drawProbability = drawProbability;
    }

    public static GameInfo getDefaultGameInfo() {
        // We return a fresh copy since we have public setters that can mutate state
        return new GameInfo(defaultInitialMean,
                            defaultInitialStandardDeviation,
                            defaultBeta,
                            defaultDynamicsFactor,
                            defaultDrawProbability);
    }
    
    public Rating getDefaultRating() {
    	return new Rating(initialMean, initialStandardDeviation);
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "initialMean=" + initialMean +
                ", initialStandardDeviation=" + initialStandardDeviation +
                ", beta=" + beta +
                ", dynamicsFactor=" + dynamicsFactor +
                ", drawProbability=" + drawProbability +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameInfo gameInfo = (GameInfo) o;

        if (Double.compare(gameInfo.initialMean, initialMean) != 0) return false;
        if (Double.compare(gameInfo.initialStandardDeviation, initialStandardDeviation) != 0) return false;
        if (Double.compare(gameInfo.beta, beta) != 0) return false;
        if (Double.compare(gameInfo.dynamicsFactor, dynamicsFactor) != 0) return false;
        return Double.compare(gameInfo.drawProbability, drawProbability) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(initialMean);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(initialStandardDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(beta);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dynamicsFactor);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(drawProbability);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static double getDefaultInitialMean() {
        return defaultInitialMean;
    }

    public static double getDefaultBeta() {
        return defaultBeta;
    }

    public static double getDefaultDrawProbability() {
        return defaultDrawProbability;
    }

    public static double getDefaultDynamicsFactor() {
        return defaultDynamicsFactor;
    }

    public static double getDefaultInitialStandardDeviation() {
        return defaultInitialStandardDeviation;
    }

    public double getInitialMean() {
        return initialMean;
    }

    public void setInitialMean(double initialMean) {
        this.initialMean = initialMean;
    }

    public double getInitialStandardDeviation() {
        return initialStandardDeviation;
    }

    public void setInitialStandardDeviation(double initialStandardDeviation) {
        this.initialStandardDeviation = initialStandardDeviation;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getDynamicsFactor() {
        return dynamicsFactor;
    }

    public void setDynamicsFactor(double dynamicsFactor) {
        this.dynamicsFactor = dynamicsFactor;
    }

    public double getDrawProbability() {
        return drawProbability;
    }

    public void setDrawProbability(double drawProbability) {
        this.drawProbability = drawProbability;
    }
}