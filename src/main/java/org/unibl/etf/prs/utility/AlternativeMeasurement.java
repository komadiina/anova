package org.unibl.etf.prs.utility;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class AlternativeMeasurement {
    public List<Double> measurements = new ArrayList<>();

    public void addMeasure(Double value) {
        measurements.add(value);
    }
}