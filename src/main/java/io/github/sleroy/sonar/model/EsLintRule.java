package io.github.sleroy.sonar.model;

import org.sonar.api.server.debt.DebtRemediationFunction;

public class EsLintRule {
    private String key;
    private String name;
    private String severity;
    private String htmlDescription;

    private boolean hasDebtRemediation;
    private DebtRemediationFunction.Type debtRemediationFunction;
    private String debtRemediationScalar;
    private String debtRemediationOffset;
    private String debtType;


    private String tags = "";

    public EsLintRule(
            String key,
            String severity,
            String name,
            String htmlDescription
    ) {
        setKey(key);
        setSeverity(severity);
        setName(name);
        setHtmlDescription(htmlDescription);

        this.setDebtRemediationFunction(DebtRemediationFunction.Type.CONSTANT_ISSUE);
        this.setDebtRemediationScalar("0min");
        this.setDebtRemediationOffset("0min");
        this.setDebtType(null);
    }

    public EsLintRule(
            String key,
            String severity,
            String name,
            String htmlDescription,
            DebtRemediationFunction.Type debtRemediationFunction,
            String debtRemediationScalar,
            String debtRemediationOffset,
            String debtType
    ) {
        setKey(key);
        setSeverity(severity);
        setName(name);
        setHtmlDescription(htmlDescription);

        this.setHasDebtRemediation(true);
        setDebtRemediationFunction(debtRemediationFunction);
        setDebtRemediationScalar(debtRemediationScalar);
        setDebtRemediationOffset(debtRemediationOffset);
        setDebtType(debtType);
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return this.severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getHtmlDescription() {
        return this.htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public boolean isHasDebtRemediation() {
        return this.hasDebtRemediation;
    }

    public void setHasDebtRemediation(boolean hasDebtRemediation) {
        this.hasDebtRemediation = hasDebtRemediation;
    }

    public DebtRemediationFunction.Type getDebtRemediationFunction() {
        return this.debtRemediationFunction;
    }

    public void setDebtRemediationFunction(DebtRemediationFunction.Type debtRemediationFunction) {
        this.debtRemediationFunction = debtRemediationFunction;
    }

    public String getDebtRemediationScalar() {
        return this.debtRemediationScalar;
    }

    public void setDebtRemediationScalar(String debtRemediationScalar) {
        this.debtRemediationScalar = debtRemediationScalar;
    }

    public String getDebtRemediationOffset() {
        return this.debtRemediationOffset;
    }

    public void setDebtRemediationOffset(String debtRemediationOffset) {
        this.debtRemediationOffset = debtRemediationOffset;
    }

    public String getDebtType() {
        return this.debtType;
    }

    public void setDebtType(String debtType) {
        this.debtType = debtType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "EsLintRule{" +
                "key='" + this.key + '\'' +
                ", name='" + this.name + '\'' +
                ", severity='" + this.severity + '\'' +
                ", htmlDescription='" + this.htmlDescription + '\'' +
                ", hasDebtRemediation=" + this.hasDebtRemediation +
                ", debtRemediationFunction=" + this.debtRemediationFunction +
                ", debtRemediationScalar='" + this.debtRemediationScalar + '\'' +
                ", debtRemediationOffset='" + this.debtRemediationOffset + '\'' +
                ", debtType='" + this.debtType + '\'' +
                ", tags='" + this.tags + '\'' +
                '}';
    }

    /**
     * Returns the list of tags as an array.
     * @return the list of tags
     */
    public String[] getTagsAsArray() {
        return this.tags.split(",");
    }
}
