package io.github.sleroy.sonar.model;


import org.apache.commons.lang3.StringUtils;
import org.sonar.api.server.debt.DebtRemediationFunction.Type;

public class EsLintRule {
    private String key;
    private String name;
    private String severity;
    private String htmlDescription;

    private boolean hasDebtRemediation;
    private Type debtRemediationFunction;
    private String debtRemediationScalar;
    private String debtRemediationOffset;
    private String debtType;


    private String tags = "";

    public EsLintRule(
            String key,
            String severity,
            String name,
            String htmlDescription,
            String tags
    ) {
        this.setKey(key);
        this.setSeverity(severity);
        this.setName(name);
        this.setHtmlDescription(htmlDescription);
        this.setTags(tags);

        setDebtRemediationFunction(Type.CONSTANT_ISSUE);
        setDebtRemediationScalar("0min");
        setDebtRemediationOffset("0min");
        setDebtType(null);
    }

    public EsLintRule(
            String key,
            String severity,
            String name,
            String htmlDescription,
            Type debtRemediationFunction,
            String debtRemediationScalar,
            String debtRemediationOffset,
            String debtType,
            String tags
    ) {
        this.setKey(key);
        this.setSeverity(severity);
        this.setName(name);
        this.setHtmlDescription(htmlDescription);
        this.setTags(tags);

        setHasDebtRemediation(true);
        this.setDebtRemediationFunction(debtRemediationFunction);
        this.setDebtRemediationScalar(debtRemediationScalar);
        this.setDebtRemediationOffset(debtRemediationOffset);
        this.setDebtType(debtType);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public boolean isHasDebtRemediation() {
        return hasDebtRemediation;
    }

    public void setHasDebtRemediation(boolean hasDebtRemediation) {
        this.hasDebtRemediation = hasDebtRemediation;
    }

    public Type getDebtRemediationFunction() {
        return debtRemediationFunction;
    }

    public void setDebtRemediationFunction(Type debtRemediationFunction) {
        this.debtRemediationFunction = debtRemediationFunction;
    }

    public String getDebtRemediationScalar() {
        return debtRemediationScalar;
    }

    public void setDebtRemediationScalar(String debtRemediationScalar) {
        this.debtRemediationScalar = debtRemediationScalar;
    }

    public String getDebtRemediationOffset() {
        return debtRemediationOffset;
    }

    public void setDebtRemediationOffset(String debtRemediationOffset) {
        this.debtRemediationOffset = debtRemediationOffset;
    }

    public String getDebtType() {
        return debtType;
    }

    public void setDebtType(String debtType) {
        this.debtType = debtType;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "EsLintRule{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", severity='" + severity + '\'' +
                ", htmlDescription='" + htmlDescription + '\'' +
                ", hasDebtRemediation=" + hasDebtRemediation +
                ", debtRemediationFunction=" + debtRemediationFunction +
                ", debtRemediationScalar='" + debtRemediationScalar + '\'' +
                ", debtRemediationOffset='" + debtRemediationOffset + '\'' +
                ", debtType='" + debtType + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    /**
     * Returns the list of tags as an array.
     *
     * @return the list of tags
     */
    public String[] getTagsAsArray() {
        if (StringUtils.isEmpty(this.tags)) return new String[0];


        return tags.trim().split(",");
    }
}
