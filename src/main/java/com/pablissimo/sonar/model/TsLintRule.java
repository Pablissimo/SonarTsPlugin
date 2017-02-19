package com.pablissimo.sonar.model;

import org.sonar.api.server.debt.DebtRemediationFunction;

public class TsLintRule {
    public final String key;
    public final String name;
    public final String severity;
    public final String htmlDescription;

    public final boolean hasDebtRemediation;
    public final DebtRemediationFunction.Type debtRemediationFunction;
    public final String debtRemediationScalar;
    public final String debtRemediationOffset;
    public final String debtType;

    public TsLintRule(
        String key,
        String severity,
        String name,
        String htmlDescription
    ) {
        this.key = key;
        this.severity = severity;
        this.name = name;
        this.htmlDescription = htmlDescription;
        
        this.hasDebtRemediation = false;
        this.debtRemediationFunction = DebtRemediationFunction.Type.CONSTANT_ISSUE;
        this.debtRemediationScalar = "0min";
        this.debtRemediationOffset = "0min";
        this.debtType = null;
    }

    public TsLintRule(
        String key,
        String severity,
        String name,
        String htmlDescription,
        DebtRemediationFunction.Type debtRemediationFunction,
        String debtRemediationScalar,
        String debtRemediationOffset,
        String debtType
    ) {
        this.key = key;
        this.severity = severity;
        this.name = name;
        this.htmlDescription = htmlDescription;

        this.hasDebtRemediation = true;
        this.debtRemediationFunction = debtRemediationFunction;
        this.debtRemediationScalar = debtRemediationScalar;
        this.debtRemediationOffset = debtRemediationOffset;
        this.debtType = debtType;
    }
}
