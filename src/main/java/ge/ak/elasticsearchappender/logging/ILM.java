package ge.ak.elasticsearchappender.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elasticsearch.appender.elastic.iml")
public class ILM {
    private String policyName;
    private String hotPhase;
    private String warmPhase;
    private String coldPhase;
    private String deletePhase;

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getHotPhase() {
        return hotPhase;
    }

    public void setHotPhase(String hotPhase) {
        this.hotPhase = hotPhase;
    }

    public String getWarmPhase() {
        return warmPhase;
    }

    public void setWarmPhase(String warmPhase) {
        this.warmPhase = warmPhase;
    }

    public String getColdPhase() {
        return coldPhase;
    }

    public void setColdPhase(String coldPhase) {
        this.coldPhase = coldPhase;
    }

    public String getDeletePhase() {
        return deletePhase;
    }

    public void setDeletePhase(String deletePhase) {
        this.deletePhase = deletePhase;
    }
}
