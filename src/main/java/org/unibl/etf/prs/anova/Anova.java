package org.unibl.etf.prs.anova;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sourceforge.jdistlib.F;
import net.sourceforge.jdistlib.T;
import org.unibl.etf.prs.utility.AlternativeMeasurement;
import org.unibl.etf.prs.utility.Measurements;
import org.unibl.etf.prs.utility.Pair;

import java.util.*;
import java.util.stream.DoubleStream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anova {
    private AnovaParameters params = new AnovaParameters();
    private Measurements dataSet;

    public Anova(Measurements m) {
        this.dataSet = m;

        this.params.n = m.getN();
        this.params.k = m.getK();
    }

    public void analyze(double alpha) {
        // 1. Column-means
        means();

        // 2. Total-mean
        totalMean();

        // 3. Errors
        errors();

        // 4. Effects
        effects();

        // 5. Sum of squares
        SSA();
        SSE();
        SST();

        // 6. Degrees of freedom
        params.calculateDFA();
        params.calculateDFE();
        params.calculateDFT();

        // 7. Contrasts
        contrasts();

        // 8. Sum of squares' variances
        varAlternatives();
        varErrors();
        varTotal();

        // 9. F-test
        fTest(alpha);

        // 10. Contrast analysis
        assessContrasts(alpha);

        // (bonus) 11. Analyze sum of squares
        System.out.printf("%.2f%% ukupne varijacije u mjerenjima je zbog razlika izmedju alternativa.\n",
                SSA() / SST() * 100.0
        );

        System.out.printf("%.2f%% ukupne varijacije u mjerenjima je zbog gresaka u mjerenjima.\n",
                SSE() / SST() * 100.0
        );
    }

    public Double SSA() {
        if (params.SSA != null)
            return params.SSA;

        double sqSum = 0.0, meanTotal = this.totalMean();

        for (int j = 0; j < params.k; j++)
            sqSum += Math.pow(mean(j) - meanTotal, 2);

        params.SSA = params.n * sqSum;
        return params.SSA;
    }

    public Double SSE() {
        if (params.SSE != null)
            return params.SSE;

        double sqSum = 0.0;

        for (int j = 0; j < params.k; j++) {
            double mean = this.mean(j);

            for (int i = 0; i < params.n; i++)
                sqSum += Math.pow(this.dataSet.getIndexedValue(i, j) - mean, 2);
        }

        params.SSE = sqSum;
        return sqSum;
    }

    public Double SST() {
        if (params.SST != null)
            return params.SST;

        params.SST = this.SSA() + this.SSE();
        return params.SST;
    }

    public Double mean(final int alternativeId) {
        if (params.means.get(alternativeId) != null)
            return params.means.get(alternativeId);

        AlternativeMeasurement column = dataSet.getAlternatives().get(alternativeId);

        params.means.put(alternativeId, column.getMeasurements().stream()
                .reduce(Double::sum).get()
                / column.getMeasurements().size());

        return params.means.get(alternativeId);
    }

    public List<Double> means() {
        List<Double> means = new ArrayList<>();

        for (int i = 0; i < params.k; i++)
            means.add(mean(i));

        return means;
    }

    public Double totalMean() {
        if (params.totalMean != null)
            return params.totalMean;

        params.totalMean =
                dataSet.getAlternatives().stream()
                        .flatMapToDouble(x -> x.getMeasurements().stream()
                                .flatMapToDouble(DoubleStream::of)).reduce(Double::sum).getAsDouble()
                        / (params.n * params.k);

        return params.totalMean;
    }

    public Double error(int i, int j) {
        // couldnt brainstorm to optimize lawl

        Double measuredValue = this.dataSet.getAlternatives().get(i).getMeasurements().get(j);
        return measuredValue - this.mean(i);
    }

    public List<Double> errors() {
        List<Double> errs = new ArrayList<>();

        for (int i = 0; i < params.k; i++)
            for (int j = 0; j < params.n; j++)
                errs.add(error(i, j));

        return errs;
    }

    public Double effect(int i) {
        if (params.effects.get(i) != null)
            return params.effects.get(i);

        params.effects.put(i, mean(i) - totalMean());
        return params.effects.get(i);
    }

    public Map<Integer, Double> effects() {
        Map<Integer, Double> effs = new HashMap<>();

        for (int i = 0; i < dataSet.getAlternatives().size(); i++)
            effs.put(i, effect(i));

        return effs;
    }

    public Pair<String, Double> contrast(int alt1, int alt2) {
        Pair<String, Double> contrast = new Pair<>();

        contrast.first = String.format("%d%d", alt1, alt2);

        if (alt1 != alt2)
            contrast.second = params.effects.get(alt1) - params.effects.get(alt2);
        else
            contrast.second = 0.0;

        return contrast;
    }

    public Map<String, Double> contrasts() {
        if (params.contrasts != null)
            return params.contrasts;

        params.contrasts = new HashMap<>();
        for (int i = 0; i < params.k; i++)
            for (int j = 0; j < params.n; j++) {
                Pair<String, Double> result = contrast(i, j);
                params.contrasts.put(result.first, result.second);
            }

        return params.contrasts;
    }

    public Double varErrors() {
        if (params.sE != null)
            return params.sE;

        params.sE = Math.pow(params.SSE / params.calculateDFE(), 2);
        return params.sE;
    }

    public Double varAlternatives() {
        if (params.sA != null)
            return params.sA;

        params.sA = Math.pow(params.SSA / params.calculateDFA(), 2);
        return params.sA;
    }

    public Double varTotal() {
        if (params.sT != null)
            return params.sT;

        params.sT = Math.pow(params.SST / params.calculateDFT(), 2);
        return params.sT;
    }

    public void fTest(double alpha) {
        if (params.fComputed == null)
            params.fComputed = params.sA / params.sE;

        if (params.fTabulated == null) {
            F fDistrib = new F(params.dfA, params.dfE);
            params.fTabulated = fDistrib.inverse_survival((1.0 - alpha) * 100.0, false);
        }

        if (params.fComputed > params.fTabulated) {
            System.out.printf("%.2f vjerovatnoca da je varijacija zbog pravih razlika u alternativama.\n",
                    (1.0 - alpha) * 100.0);
        } else {
            System.out.println("Nemoguce dedukovati uzrok varijacije pomocu Fischer-ovog testa; varijacije nisu neophodno znacajne.");
        }
    }

    public void assessContrasts(Double alpha) {
        double probability = (1.0 - alpha) * 100.0;
        T student = new T(params.dfE);
        Double tVal = student.cumulative(1 - (1 - probability) / 2);

        for (Map.Entry<String, Double> entry : params.contrasts.entrySet()) {
            if (entry.getValue() != 0.0) {
                Integer alt1 = Integer.parseInt(String.format("%c", entry.getKey().charAt(0)));
                Integer alt2 = Integer.parseInt(String.format("%c", entry.getKey().charAt(1)));

                Double contrast1 = params.effects.get(alt1),
                        contrast2 = params.effects.get(alt2);

                double c = contrast1 + contrast2;
                Double sC = params.sE * Math.sqrt(2.0 / (params.k * params.n));

                Double c1 = c + tVal * sC, c2 = c - tVal * sC;
                List<Double> interval = Arrays.asList(c1, c2);
                interval.sort(Double::compare);

                if (interval.get(0) * interval.get(1) <= 0)
                    System.out.printf(
                            "Kontrast sistema [%d, %d] nije od statistickog znacaja, {c1, c2} = {%.5f, %.5f}.\n",
                            alt1, alt2, interval.get(0), interval.get(1)
                    );

                else System.out.printf(
                        "%.2f-tni interval povjerenja za [%d, %d]: {%.5f, %.5f}.\n",
                        probability, alt1, alt2, interval.get(0), interval.get(1)
                );
            }
        }
    }
}