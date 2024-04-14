package org.unibl.etf.prs.anova;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class AnovaParameters {
    public Integer n = null, k = null;
    public Map<Integer, Double> means = new HashMap<>(), effects = new HashMap<>();
    public Double totalMean = null;
    public Double SSA = null, SSE = null, SST = null;
    public Integer dfA = null, dfE = null, dfT = null;
    public Map<String, Double> contrasts = new HashMap<>();
    public Double sA = null, sE = null, sT = null; // variances (mean square value)
    public Double fComputed = null;
    public Double fTabulated = null;

    public Integer calculateDFA() {
        this.dfA = this.k - 1;
        return this.dfA;
    }

    public Integer calculateDFE() {
        this.dfE = k * (n - 1);
        return this.dfE;
    }

    public Integer calculateDFT() {
        this.dfT = k * n - 1;
        return this.dfT;
    }
}
