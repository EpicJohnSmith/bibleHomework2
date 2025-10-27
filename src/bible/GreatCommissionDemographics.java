package bible;

import java.util.Arrays;

public class GreatCommissionDemographics {

    // PARAMETERS (change them to run alternate scenarios)
    static final double INITIAL_POPULATION = 7_700_000_000.0; // 7.7 billion
    static final int MAX_AGE = 72;    // lifespan in years -> ages 0..71
    static final int LAST_AGE = MAX_AGE - 1;
    static final double TRAINED_PER_3_YEARS = 2.0; // each trainer makes 2 disciples every 3 years
    static final double TRAIN_PER_YEAR_RATE = TRAINED_PER_3_YEARS / 3.0; // per-year rate
    static final int START_TRAIN_AGE = 18; // must be >= 18 to be a trainer
    static final int BIRTH_AGE = 30; // couples at age 30 have one baby (per couple)
    static final int INITIAL_DISCIPLES = 13;
    static final int INITIAL_DISCIPLE_AGE = 30; // assumed age for initial disciples
    static final int MAX_YEARS = 2000; // safety cap

    public static void main(String[] args) {
        double[] pop = new double[MAX_AGE];       // non-disciples at each age
        double[] disciples = new double[MAX_AGE]; // disciples at each age

        // Initialize population uniformly across ages
        double perAge = INITIAL_POPULATION / MAX_AGE;
        Arrays.fill(pop, perAge);

        // Place initial disciples into chosen age cohort (subtract from non-disciples)
        disciples[INITIAL_DISCIPLE_AGE] = INITIAL_DISCIPLES;
        pop[INITIAL_DISCIPLE_AGE] = Math.max(0.0, pop[INITIAL_DISCIPLE_AGE] - INITIAL_DISCIPLES);

        int year = 0;
        while (year < MAX_YEARS) {
            double totalPopBefore = total(pop) + total(disciples);
            double totalDisciplesBefore = total(disciples);

            // Check stop condition: everyone alive is a disciple (allow tiny epsilon for doubles)
            if (totalDisciplesBefore + 1e-9 >= totalPopBefore) {
                // recompute and print final totals to be explicit
                double totalPop = total(pop) + total(disciples);
                double totalDisciples = total(disciples);
                System.out.printf("All living people are disciples after %d years.%n", year);
                System.out.printf("Year %d: population=%.0f, disciples=%.0f%n", year, totalPop, totalDisciples);
                break;
            }

            // TRAINING STEP (during the year)
            double trainers = 0.0;
            for (int a = START_TRAIN_AGE; a <= LAST_AGE; a++) trainers += disciples[a];

            double conversionsThisYear = trainers * TRAIN_PER_YEAR_RATE;

            double nonDisciplesTotal = total(pop);
            if (conversionsThisYear > nonDisciplesTotal) conversionsThisYear = nonDisciplesTotal;

            if (nonDisciplesTotal > 0.0 && conversionsThisYear > 0.0) {
                // distribute conversions proportionally by age
                for (int a = 0; a <= LAST_AGE; a++) {
                    double fraction = pop[a] / nonDisciplesTotal;
                    double conv = conversionsThisYear * fraction;
                    pop[a] -= conv;
                    disciples[a] += conv;
                }
            }

            // AGEING STEP: shift cohorts up one year (oldest die)
            double[] newPop = new double[MAX_AGE];
            double[] newDisciples = new double[MAX_AGE];

            for (int a = LAST_AGE - 1; a >= 0; a--) {
                newPop[a + 1] = pop[a];
                newDisciples[a + 1] = disciples[a];
            }

            // BIRTHS: couples at birth age after ageing produce children
            double peopleAtBirthAge = newPop[BIRTH_AGE] + newDisciples[BIRTH_AGE];
            // Decide: fractional births (consistent with doubles) or integer births (Math.floor).
            // Here we use fractional births to be consistent with the double model:
            double births = peopleAtBirthAge / 2.0; // one baby per couple on average
            newPop[0] = births;

            // Replace arrays
            pop = newPop;
            disciples = newDisciples;

            year++;

            // Diagnostics: first 10 years, then every 10 years
            if (year <= 10 || year % 10 == 0) {
                double totalPop = total(pop) + total(disciples);
                double totalDisciples = total(disciples);
                System.out.printf("Year %4d: population=%.0f; disciples=%.0f; pct=%.4f%n",
                        year, totalPop, totalDisciples, totalDisciples / totalPop);
            }
        }

        if (year >= MAX_YEARS) {
            double totalPop = total(pop) + total(disciples);
            double totalDisciples = total(disciples);
            System.out.printf("Reached max years (%d). population=%.0f, disciples=%.0f, pct=%.6f%n",
                    MAX_YEARS, totalPop, totalDisciples, totalDisciples / totalPop);
        }
    }

    static double total(double[] arr) {
        double s = 0.0;
        for (double v : arr) s += v;
        return s;
    }
}