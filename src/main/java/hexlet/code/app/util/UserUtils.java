package hexlet.code.app.util;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

  private final UserRepository userRepository;

  public UserUtils(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isCurrentUser(Long id) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    User user = userRepository.findById(id).orElse(null);

    if (user == null) {
      return false;
    }

    return user.getEmail().equals(authentication.getName());
  }
}
