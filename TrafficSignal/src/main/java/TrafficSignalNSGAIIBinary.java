import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;

import java.util.BitSet;

public class TrafficSignalNSGAIIBinary extends AbstractBinaryProblem {
    private final double trafficJam[] = {30, 60, 10, 70};
    /**
     * speed in terms of exporting unit traffic volume per second.
     */
    private final double heteroSpeed[] = {3.4, 3, 1.4, 2.2};


    private final int bits, dmin;
    private final double factor;

    public TrafficSignalNSGAIIBinary(Integer numberOfBits, int minDomain, int maxDomain) {
        setNumberOfVariables(4);
        setNumberOfObjectives(2);
        setName("TrafficSignal");

        bits = numberOfBits;
        dmin = minDomain;
        factor = (maxDomain - dmin) / (Math.pow(2, bits) - 1);
    }

    @Override
    protected int getBitsPerVariable(int index) {
        return bits;
    }

    @Override
    public void evaluate(BinarySolution solution) {
        double[] objectives = new double[getNumberOfObjectives()];

        int numberOfVariables = getNumberOfVariables();
        int[] candidate = new int[numberOfVariables];
        for (int var = 0; var < numberOfVariables; var++) {
            candidate[var] = (int) Math.round(dmin + bitSetToInt(solution.getVariableValue(var)) * factor);
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

    private int bitSetToInt(BitSet bitSet) {
        int bitInteger = 0;
        for (int i = 0; i < bits; i++) {
            if (bitSet.get(i)) {
                bitInteger |= (1 << i);
            }
        }
        return bitInteger;
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