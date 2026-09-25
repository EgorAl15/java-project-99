package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.dto.UserResponseDto;
import hexlet.code.app.dto.UserUpdateDto;
import java.util.List;

public interface UserService {

  List<UserResponseDto> getAll();

  UserResponseDto getById(Long id);

  UserResponseDto create(UserCreateDto dto);

  UserResponseDto update(Long id, UserUpdateDto dto);

  void delete(Long id);
}
