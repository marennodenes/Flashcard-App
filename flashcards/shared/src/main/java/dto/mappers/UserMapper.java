package dto.mappers;

import dto.UserDataDto;
import app.User;

public class UserMapper {

  public UserDataDto toDto(User user) {
    return new UserDataDto(user.getUsername(), user.getPassword());

  } 

  public User fromDto(UserDataDto userDataDto) {
    return new User(userDataDto.getUsername(), userDataDto.getPassword());
  }
}
