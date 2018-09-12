import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.util.Arrays;
import java.util.List;

/**
 * Class to configure and run a generational genetic algorithm. The target problem TrafficSignalGA.
 *
 * @author Tarik Reza Toha <1017052013@grad.cse.buet.ac.bd>
 */
public class TrafficSignalRunner extends AbstractAlgorithmRunner {


    private DoubleProblem problem;
    private CrossoverOperator<DoubleSolution> crossover;
    private MutationOperator<DoubleSolution> mutation;
    private SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    private AlgorithmRunner algorithmRunner;

    public static void main(String[] args) throws Exception {
//        new TrafficSignalRunner().optimizerGA();
        new TrafficSignalRunner().optimizerNSGAII();
    }

    private void optimizerNSGAII() {
        problem = new TrafficSignalNSGAII(4, 0, 180);

        crossover = new SBXCrossover(0.9, 20.0);
        mutation = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20);
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setPopulationSize(100)
                .setMaxEvaluations(1000)
                .build();

        algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        List<DoubleSolution> population = algorithm.getResult();

        long computingTime = algorithmRunner.getComputingTime();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
    }

    private void optimizerGA() {
        problem = new TrafficSignalGA(4, 0, 180);

        crossover = new SBXCrossover(0.9, 20);
        mutation = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20);
        selection = new BinaryTournamentSelection<>();

        Algorithm<DoubleSolution> algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                .setPopulationSize(100)
                .setMaxEvaluations(1000)
                .setSelectionOperator(selection)
                .build();

        algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        DoubleSolution solution = algorithm.getResult();

        double[] x = new double[solution.getNumberOfVariables()];
        for (int i = 0; i < x.length; i++) x[i] = solution.getVariableValue(i);
        System.out.println(Arrays.toString(x));
        double[] finalState = ((TrafficSignalGA) problem).finalState(x);
        System.out.println(Arrays.toString(finalState) + "; " + solution.getObjective(0));
    }
}
