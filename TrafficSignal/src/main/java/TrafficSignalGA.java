import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrafficSignalGA extends AbstractDoubleProblem {
    private double trafficJam[] = {3, 6, 1, 7};

    /**
     * Constructor Creates a default instance of the traffic signal optimization
     * problem
     *
     * @param numberOfLinks Number of links of the junction
     * @param minJamTime    Minimum time of traffic jam
     * @param maxJamTime    Maximum time of traffic jam
     */
    public TrafficSignalGA(int numberOfLinks, double minJamTime, double maxJamTime) {
        setNumberOfVariables(numberOfLinks);
        setNumberOfObjectives(1);
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
        int numberOfVariables = getNumberOfVariables();

        double[] x = new double[numberOfVariables];

        for (int i = 0; i < numberOfVariables; i++) {
            x[i] = solution.getVariableValue(i);
        }

        double sum = 0.0;
        for (int var = 0; var < numberOfVariables; var++) {
            double value = x[var];
            sum += Math.abs(trafficJam[var] - value / 60);
        }

        solution.setObjective(0, sum);
    }

    public double[] finalState(double[] best) {
        for (int i = 0; i < best.length; i++) {
            trafficJam[i] -= best[i] / 60;
        }
        return trafficJam;
    }
}
