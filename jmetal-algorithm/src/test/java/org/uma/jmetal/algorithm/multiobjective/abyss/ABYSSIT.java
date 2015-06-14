package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by ajnebro on 11/6/15.
 */
public class ABYSSIT {
  Algorithm<List<DoubleSolution>> algorithm;
  DoubleProblem problem ;
  CrossoverOperator<DoubleSolution> crossover;
  MutationOperator<DoubleSolution> mutation;
  LocalSearchOperator<DoubleSolution> localSearchOperator ;
  Archive<DoubleSolution> archive ;

  @Before
  public void setup() {
    problem = new ZDT4() ;

    double crossoverProbability = 1.0 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    archive = new CrowdingDistanceArchive<>(100) ;

    localSearchOperator = new AbYSSLocalSearch<>(1, mutation, archive, problem) ;
  }

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    algorithm = new ABYSSBuilder(problem, archive)
        .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and AbYSS, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98) ;
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    algorithm = new ABYSSBuilder(problem, archive)
        .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();

    Hypervolume hypervolume = new Hypervolume() ;

    // Rationale: the default problem is ZDT4, and MOCell, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.65

    Front referenceFront = new ArrayFront() ;
    referenceFront.readFrontFromFile("/referenceFronts/ZDT4.pf");

    double hv = hypervolume.execute(new ArrayFront(population), referenceFront) ;

    assertTrue(hv > 0.65) ;
  }
}