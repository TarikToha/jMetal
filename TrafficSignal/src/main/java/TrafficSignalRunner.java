import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
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
    private long computingTime;

    public static void main(String[] args) {
//        new TrafficSignalRunner().optimizerGA();
        new TrafficSignalRunner().optimizerNSGAII();
//        new TrafficSignalRunner().optimizerNSGAIIBinary();
    }

    private void optimizerNSGAIIBinary() {

        BinaryProblem problem = new TrafficSignalNSGAIIBinary(10, 25, 130);

        CrossoverOperator<BinarySolution> crossover = new HUXCrossover(0.9);
        MutationOperator<BinarySolution> mutation = new BitFlipMutation(1.0 / problem.getNumberOfBits(0));
        SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation)
                .setPopulationSize(100)
                .setMaxEvaluations(1000)
                .setSelectionOperator(selection)
                .build();

        algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        List<BinarySolution> population = algorithm.getResult();
        printFinalSolutionSet(population);
    }

    private void optimizerNSGAII() {
        IntegerProblem problem = new TrafficSignalNSGAII(4, 0, 180);

        CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(1.0 / problem.getNumberOfVariables(), 20.0);
        MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(1.0 / problem.getNumberOfVariables(), 20);
        SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());
//        SolutionListEvaluator<DoubleSolution> evaluator = new MultithreadedSolutionListEvaluator<>(4, problem);

        Algorithm<List<IntegerSolution>> algorithm = new NSGAIIBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
//                .setSolutionListEvaluator(evaluator)
                .setPopulationSize(100)
                .setMaxEvaluations(10000)
                .build();

        algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        List<IntegerSolution> population = algorithm.getResult();
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
