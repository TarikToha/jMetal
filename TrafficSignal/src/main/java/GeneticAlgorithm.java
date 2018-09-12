
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class GeneticAlgorithm {

    private final int populationSize = 100;

    private final Random rand = new Random();
    private final double speed = 60;
    private final int numberOfLinks = 4;
    private final double prob = 1.0 / numberOfLinks;
    private final TrafficJam trafficJam = new TrafficJam(new double[]{3, 6, 1, 7});

    public static void main(String[] args) throws Exception {
        new GeneticAlgorithm().driver();
    }

    private SignalCycle runGeneticAlgorithm(int maxGeneration) throws Exception {
        //P <- {}
        HashSet<SignalCycle> population = new HashSet<>();

        //for popsize times do
        for (int i = 0; i < populationSize; i++) {
            //P <- P U {new random individual}
            population.add(new SignalCycle());
        }

        //Best <- null
        SignalCycle best = null;

        //repeat
        //until Best is the ideal solution or we have run out of time
        for (int numberOfGeneration = 0; numberOfGeneration < maxGeneration; numberOfGeneration++) {
            //for each individual Pi in P do
            ArrayList<SignalCycle> arrayList = new ArrayList<>(population);
            for (int i = 0; i < populationSize; i++) {
                //AssessFitness(Pi)
                SignalCycle signalCycle = arrayList.get(i);
                signalCycle.fitness = assessFitness(signalCycle);

                //if Best = null or Fitness(Pi) > Fitness(Best) then
                //    Best <- Pi
                if (best == null || signalCycle.fitness < best.fitness) {
                    best = signalCycle;
                }
            }

            //Q <— {}
            HashSet<SignalCycle> offspring = new HashSet<>();

            //for popsize /2 times do
            for (int i = 0; i < populationSize / 2; i++) {

                //Parent Pa <— SelectWithReplacement(P)
                SignalCycle father = selectWithReplacement(population);

                //Parent Pb <— SelectWithReplacement(P)
                SignalCycle mother = selectWithReplacement(population);

                //Children Ca,Cb <- Crossover(Copy(Pa), Copy(Pb))
                SignalCycle children[] = crossover(father.clone(), mother.clone());

                //   Q <— Q U {Mutate(Ca), Mutate(Cb)}
                offspring.add(mutation(children[0]));
                offspring.add(mutation(children[1]));

            }
            //P <- Q
            population = offspring;
        }
        // return Best
        return best;
    }

    private double assessFitness(SignalCycle signalCycle) {
        trafficJam.updateLinks(signalCycle);
        return trafficJam.getScore();
    }

    private SignalCycle selectWithReplacement(HashSet<SignalCycle> offspring) {
        int tournamentSize = numberOfLinks;

        ArrayList<SignalCycle> arrayList = new ArrayList<>(offspring);

        SignalCycle best = arrayList.get(rand.nextInt(arrayList.size()));

        for (int i = 1; i < tournamentSize; i++) {
            SignalCycle next = arrayList.get(rand.nextInt(arrayList.size()));

            if (next.fitness > best.fitness) {
                best = next;
            }
        }
        return best;

    }

    private SignalCycle[] crossover(Object parent1, Object parent2) {
        SignalCycle father = (SignalCycle) parent1, mother = (SignalCycle) parent2;

        int temp[] = new int[numberOfLinks];
        for (int i = 0; i < temp.length; i++) {
            if (prob >= rand.nextDouble()) {
                for (int j = 0; j < temp.length; j++) {
                    temp[j] = (int) Math.round((father.signalPhases[i][j] + mother.signalPhases[i][j]) / 2.0);
                    mother.signalPhases[i][j] = father.signalPhases[i][j] = temp[j];
                }
            }
        }

        return new SignalCycle[]{father, mother};
    }

    private SignalCycle mutation(SignalCycle signalCycle) {
        int noise, sign;
        for (int i = 0; i < signalCycle.signalPhases.length; i++) {
            if (prob >= rand.nextDouble()) {
                sign = rand.nextDouble() > 0.5 ? 1 : -1;
                noise = rand.nextInt(10) * sign;
                for (int j = 0; j < signalCycle.signalPhases[i].length; j++) {
                    if (signalCycle.signalPhases[i][j] >= 0) {
                        signalCycle.signalPhases[i][j] += noise;
                    } else {
                        signalCycle.signalPhases[i][j] -= noise;
                    }
                }
            }
        }
        return signalCycle;
    }

    void driver() throws Exception {
        SignalCycle solution = runGeneticAlgorithm(10000);
        System.out.println(solution);
        assessFitness(solution);
        System.out.println(trafficJam);
    }

    private class SignalCycle {

        private final int signalPhases[][] = new int[numberOfLinks][numberOfLinks];
        private Double fitness;

        public SignalCycle() {
            int signalId, signalTime;
            for (int[] signalPhase : signalPhases) {
                signalId = rand.nextInt(signalPhase.length);
                signalTime = rand.nextInt(180);
                for (int s = 0; s < signalPhase.length; s++) {
                    if (s != signalId) {
                        signalPhase[s] += signalTime;
                    } else {
                        signalPhase[s] -= signalTime;
                    }
                }
            }
        }

        @Override
        protected Object clone() {
            SignalCycle temp = new SignalCycle();
            for (int i = 0; i < numberOfLinks; i++) {
                System.arraycopy(signalPhases[i], 0, temp.signalPhases[i], 0, numberOfLinks);
            }
            temp.fitness = fitness;
            return temp;
        }

        @Override
        public String toString() {
            String signals = "";
            for (int[] signalPhase : signalPhases) {
                for (int s = 0; s < signalPhase.length; s++) {
                    signals += signalPhase[s] + ",";
                }
            }
            return signals;
        }
    }

    private class TrafficJam {

        private final double jamLength[];
        private final double updatedLength[] = new double[numberOfLinks];

        public TrafficJam(double jamLength[]) {
            this.jamLength = jamLength;
            copyArray();
            System.out.println(this.toString());
        }

        private void copyArray() {
            System.arraycopy(jamLength, 0, updatedLength, 0, numberOfLinks);
        }

        private void updateLinks(SignalCycle signalCycle) {
            copyArray();

            for (int[] signalPhase : signalCycle.signalPhases) {
                for (int s = 0; s < signalPhase.length; s++) {
                    //updatedLength[s] += rand.nextDouble();
                    if (signalPhase[s] < 0) {
                        updatedLength[s] += signalPhase[s] / speed;
                    }
                }
                //System.out.println(trafficJam);
            }
        }

        private double getScore() {
            double score = 0;

            for (int i = 0; i < updatedLength.length; i++) {
                score += Math.abs(updatedLength[i]);
            }

            return score;
        }

        @Override
        public String toString() {
            return Arrays.toString(updatedLength) + "; " + getScore();
        }

    }
}
