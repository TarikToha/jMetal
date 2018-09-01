import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Class to configure and run a generational genetic algorithm. The target problem TrafficSignalOptimizer.
 *
 * @author Tarik Reza Toha <1017052013@grad.cse.buet.ac.bd>
 */
public class TrafficSignalRunner {

    public static void main(String[] args) {
        new TrafficSignalRunner().optimizer();
    }

    private void optimizer() {
        Algorithm<DoubleSolution> algorithm;
        DoubleProblem problem = new TrafficSignalOptimizer(4, 0, 180);

        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(0.9, 20);
        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20);
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>();

        algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                .setPopulationSize(100)
                .setMaxEvaluations(1000)
                .setSelectionOperator(selection)
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        DoubleSolution solution = algorithm.getResult();
//        List<DoubleSolution> population = new ArrayList<>(1);
//        population.add(solution);

//        long computingTime = algorithmRunner.getComputingTime();
//
//        new SolutionListOutput(population)
//                .setSeparator("\t")
//                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
//                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
//                .print();

//        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
//        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
//        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

        double[] x = new double[solution.getNumberOfVariables()];
        for (int i = 0; i < x.length; i++) x[i] = solution.getVariableValue(i);
        System.out.println(Arrays.toString(x));
        double[] finalState = ((TrafficSignalOptimizer) problem).finalState(x);
        System.out.println(Arrays.toString(finalState) + "; " + solution.getObjective(0));
    }
}
