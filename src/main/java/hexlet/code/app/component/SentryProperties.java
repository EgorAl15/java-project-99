package hexlet.code.app.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SentryProperties {

  private final String authToken;

  public SentryProperties(@Value("${monitoring.sentry-auth-token:}") String authToken) {

    this.authToken = authToken;
  }

  public String getAuthToken() {
    return authToken;
  }
}
