package org.unibl.etf.prs.utility;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Measurements {
    public List<AlternativeMeasurement> alternatives = new ArrayList<>();
    @ToString.Exclude
    private Scanner scn = new Scanner(System.in);
    private Integer n, k = 0;

    public void defineAlternative(Integer numMeasurements) {
        this.k++;
        this.n = numMeasurements;

        System.out.println("< Alternative's measurements >");
        AlternativeMeasurement measured = new AlternativeMeasurement();

        for (int i = 0; i < numMeasurements; i++)
            measured.addMeasure(inputMeasurement(i + 1));

        alternatives.add(measured);
    }

    public Double inputMeasurement(Integer i) {
        System.out.printf("Measurement [%d]: ", i);
        return scn.nextDouble();
    }

    public Double getIndexedValue(int i, int j) {
        return alternatives.get(j).getMeasurements().get(i);
    }
}
