import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrafficSignalNSGAII extends AbstractDoubleProblem {
    private final double trafficJam[] = {30, 60, 10, 70};
    /**
     * speed in terms of exporting unit traffic volume per second.
     */
    private final double heteroSpeed[] = {3.4, 3, 1.4, 2.2};

    /**
     * Constructor Creates a default instance of the traffic signal optimization
     * problem
     *
     * @param numberOfLinks Number of links of the junction
     * @param minJamTime    Minimum time of traffic jam
     * @param maxJamTime    Maximum time of traffic jam
     */
    public TrafficSignalNSGAII(int numberOfLinks, double minJamTime, double maxJamTime) {
        setNumberOfVariables(numberOfLinks);
        setNumberOfObjectives(2);
        setName("TrafficSignal");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(minJamTime);
            upperLimit.add(maxJamTime);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);


        double sum = 0;
        for (double aTrafficJam : trafficJam) {
            sum += aTrafficJam;
        }
        System.out.println(Arrays.toString(trafficJam) + "; " + sum);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        double[] objectives = new double[getNumberOfObjectives()];

        int numberOfVariables = getNumberOfVariables();
        double[] candidate = new double[numberOfVariables];
        for (int var = 0; var < numberOfVariables; var++) {
            candidate[var] = solution.getVariableValue(var);
        }

        double[] updatedJam = new double[numberOfVariables];
        System.arraycopy(trafficJam, 0, updatedJam, 0, numberOfVariables);

        double sumJam = 0.0, sumTime = 0.0;
        for (int var = 0; var < numberOfVariables; var++) {
            updatedJam[var] = Math.abs(updatedJam[var] - candidate[var] * heteroSpeed[var]);
            sumJam += updatedJam[var];

            for (int i = 0; i < numberOfVariables; i++) {
                if (i != var) {
                    sumTime += candidate[var] + seriesSum(updatedJam[i]) / (1 + updatedJam[i]) / heteroSpeed[i];
                }
            }
        }

        objectives[0] = sumJam;
        objectives[1] = sumTime;
        solution.setObjective(0, objectives[0]);
        solution.setObjective(1, objectives[1]);
    }

    private double seriesSum(double max) {
        double sum = 0;
        max = Math.round(max) - 1;
        for (int i = 1; i <= max; i++) {
            sum += i;
        }
        return sum;
    }
}
