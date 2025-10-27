package bible;

import java.util.Arrays;

public class GreatCommissionDemographics
{

    // Parameters (What is AI thinking for the documentation here?)
    static final double INITIAL_POPULATION = 7_700_000_000.0; // 7.7 billion people
    static final int MAX_AGE = 72;
    static final int LAST_AGE = MAX_AGE - 1;
    static final double TRAINED_PER_3_YEARS = 2.0; // each trainer makes 2 disciples every 3 years
    static final double TRAIN_PER_YEAR_RATE = TRAINED_PER_3_YEARS / 3.0;
    static final int START_TRAIN_AGE = 18; // must be >= 18 to be a trainer
    static final int BIRTH_AGE = 30; // couples at age 30 have one baby per couple
    static final int INITIAL_DISCIPLES = 13;
    static final int INITIAL_DISCIPLE_AGE = 30; // assumed age for initial disciples
    static final int MAX_YEARS = 2000; // safety cap

    public static void main(String[] args) // Yeah, I hope AI is cooking here, because I sort of understand what it's process is
    {
        double[] pop = new double[MAX_AGE];       // here is the non-disciples at each age
        double[] disciples = new double[MAX_AGE]; // and here is the disciples at each age

        // Initialize population uniformly across ages
        double perAge = INITIAL_POPULATION / MAX_AGE;
        Arrays.fill(pop, perAge);

        // Place initial disciples into chosen age
        disciples[INITIAL_DISCIPLE_AGE] = INITIAL_DISCIPLES;
        pop[INITIAL_DISCIPLE_AGE] = Math.max(0.0, pop[INITIAL_DISCIPLE_AGE] - INITIAL_DISCIPLES);

        int year = 0;
        while (year < MAX_YEARS)
        {
            double totalPop = total(pop) + total(disciples);
            double totalDisciples = total(disciples);

            // choose a saturation threshold (e.g., 99.9%)
            double saturationThreshold = 0.995; // 99.9%

            if (totalDisciples / totalPop >= saturationThreshold) {
                System.out.printf("Saturation reached at year %d.%n", year);
                System.out.printf("Year %d: population=%.0f; disciples=%.0f; pct=%.5f%n",
                                  year, totalPop, totalDisciples, totalDisciples / totalPop);
                break;
            }

            // This is the training
            double trainers = 0.0;
            for (int a = START_TRAIN_AGE; a <= LAST_AGE; a++) trainers += disciples[a];

            double conversionsThisYear = trainers * TRAIN_PER_YEAR_RATE;

            double nonDisciplesTotal = total(pop);
            if (conversionsThisYear > nonDisciplesTotal) conversionsThisYear = nonDisciplesTotal;

            if (nonDisciplesTotal > 0.0 && conversionsThisYear > 0.0)
            {
                // distribute conversions proportionally by age
                for (int a = 0; a <= LAST_AGE; a++)
                {
                    double fraction = pop[a] / nonDisciplesTotal;
                    double conv = conversionsThisYear * fraction;
                    pop[a] -= conv;
                    disciples[a] += conv;
                }
            }

            // This is for aging
            double[] newPop = new double[MAX_AGE];
            double[] newDisciples = new double[MAX_AGE];

            for (int a = LAST_AGE - 1; a >= 0; a--)
            {
                newPop[a + 1] = pop[a];
                newDisciples[a + 1] = disciples[a];
            }

            // Couples at birth age after aging produce children
            double peopleAtBirthAge = newPop[BIRTH_AGE] + newDisciples[BIRTH_AGE];
            double births = peopleAtBirthAge / 2.0; // one baby per couple on average
            newPop[0] = births;

            // Replace arrays
            pop = newPop;
            disciples = newDisciples;

            year++;

            // Results: first 10 years, then every 10 years
            if (year <= 10 || year % 10 == 0)
            {
                double totalPop1 = total(pop) + total(disciples);
                double totalDisciples1 = total(disciples);
                System.out.printf("Year %4d: population=%.0f; disciples=%.0f; pct=%.4f%n",
                        year, totalPop1, totalDisciples1, totalDisciples1 / totalPop1);
            }
        }

        if (year >= MAX_YEARS)
        {
            double totalPop = total(pop) + total(disciples);
            double totalDisciples = total(disciples);
            System.out.printf("Reached max years (%d). population=%.0f, disciples=%.0f, pct=%.6f%n",
                    MAX_YEARS, totalPop, totalDisciples, totalDisciples / totalPop);
        }
    }

    static double total(double[] arr)
    {
        double s = 0.0;
        for (double v : arr) s += v;
        return s;
    }
}