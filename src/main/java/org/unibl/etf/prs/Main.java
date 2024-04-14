package org.unibl.etf.prs;

import org.unibl.etf.prs.anova.Anova;
import org.unibl.etf.prs.utility.Measurements;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the number of alternatives: ");
        Integer numAlternatives = in.nextInt();

        System.out.print("Enter the number of measurements: ");
        Integer numMeasurements = in.nextInt();

        Measurements alternatives = new Measurements();
        for (int i = 0; i < numAlternatives; i++) {
            System.out.printf("Alternative [#%d]:\n", i + 1);
            alternatives.defineAlternative(numMeasurements);
        }

        System.out.print("Define alpha (interval of trust coefficient, commonly 0.05): ");
        double alpha = in.nextDouble();

        Anova calculator = new Anova(alternatives);
        calculator.analyze(alpha);
    }
}
