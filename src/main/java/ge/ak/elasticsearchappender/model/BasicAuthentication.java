package ge.ak.elasticsearchappender.model;

import com.agido.logback.elasticsearch.config.Authentication;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthentication implements Authentication {
    private final String username;
    private final String password;

    public BasicAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void addAuth(HttpURLConnection urlConnection, String body) {
        String credentials = username + ":" + password;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        urlConnection.setRequestProperty("Authorization", basicAuth);
        urlConnection.setRequestProperty("Content-Type", "application/json");
    }
}
