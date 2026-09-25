package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserResponseDto;
import hexlet.code.app.dto.UserUpdateDto;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<UserResponseDto> getAll() {
    return userRepository.findAll().stream().map(this::toDto).toList();
  }

  @Override
  public UserResponseDto getById(Long id) {
    var user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return toDto(user);
  }

  @Override
  public UserResponseDto create(UserCreateDto dto) {

    var user = new User();

    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));

    return toDto(userRepository.save(user));
  }

  @Override
  public UserResponseDto update(Long id, UserUpdateDto dto) {

    var user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (dto.getFirstName() != null) {
      user.setFirstName(dto.getFirstName());
    }

    if (dto.getLastName() != null) {
      user.setLastName(dto.getLastName());
    }

    if (dto.getEmail() != null) {
      user.setEmail(dto.getEmail());
    }

    if (dto.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    return toDto(userRepository.save(user));
  }

  @Override
  public void delete(Long id) {
    var user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    userRepository.delete(user);
  }

  private UserResponseDto toDto(User user) {
    return new UserResponseDto(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getCreatedAt());
  }
}
