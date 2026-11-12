package org.springframework.boot.autoconfigure.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Compatibility stub for Flapdoodle Embedded Mongo in Spring Boot 4.
 * Flapdoodle spring30x references org.springframework.boot.autoconfigure.mongo.MongoProperties
 * to bind mongo configuration and inject dynamic port bindings.
 */
@ConfigurationProperties("spring.data.mongodb")
public class MongoProperties {
    private String host = "localhost";
    private Integer port = null;
    private String uri;
    private String database = "testdb";
    private String username;
    private char[] password;

    public static class Ssl {
        private Boolean enabled;
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }
    private final Ssl ssl = new Ssl();
    public Ssl getSsl() { return ssl; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public String getMongoClientDatabase() { return database; }
    public void setMongoClientDatabase(String database) { this.database = database; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public char[] getPassword() { return password; }
    public void setPassword(char[] password) { this.password = password; }
}
