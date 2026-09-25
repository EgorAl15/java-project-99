package hexlet.code.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-error")
public class TestErrorController {

  @GetMapping
  public void testError() {
    throw new RuntimeException("Test Sentry error");
  }
}
