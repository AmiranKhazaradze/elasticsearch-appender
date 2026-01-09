package ge.ak.elasticsearchappender.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch.appender.elastic")
public class ElasticsearchAppenderProperties {
    private Boolean enable;
    private Boolean createIndex;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String schema;
    private String actionLogIndexName;


    private String appName;
    private String logLevel;
    private String systemLogIndexName;
    private String systemLogLoggerName;
    private String systemLogErrorLoggerName;

    private ILM iml;

    public ILM getIml() {
        return iml;
    }

    public void setIml(ILM iml) {
        this.iml = iml;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getActionLogIndexName() {
        return actionLogIndexName;
    }

    public void setActionLogIndexName(String actionLogIndexName) {
        this.actionLogIndexName = actionLogIndexName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getSystemLogIndexName() {
        return systemLogIndexName;
    }

    public void setSystemLogIndexName(String systemLogIndexName) {
        this.systemLogIndexName = systemLogIndexName;
    }

    public String getSystemLogLoggerName() {
        return systemLogLoggerName;
    }

    public void setSystemLogLoggerName(String systemLogLoggerName) {
        this.systemLogLoggerName = systemLogLoggerName;
    }

    public String getSystemLogErrorLoggerName() {
        return systemLogErrorLoggerName;
    }

    public void setSystemLogErrorLoggerName(String systemLogErrorLoggerName) {
        this.systemLogErrorLoggerName = systemLogErrorLoggerName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getCreateIndex() {
        return createIndex;
    }

    public void setCreateIndex(Boolean createIndex) {
        this.createIndex = createIndex;
    }
}
