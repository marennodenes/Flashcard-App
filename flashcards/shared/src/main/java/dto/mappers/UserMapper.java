package dto.mappers;

import app.User;
import dto.UserDataDto;

/**
 * Mapper class for converting between User and UserDataDto.
 * @author @ailinat
 * @author @sofietw
 */
public class UserMapper {

  /**
   * Converts a User to a UserDataDto.
   * @param user the User to convert
   * @return the corresponding UserDataDto
   */
  public UserDataDto toDto(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    return new UserDataDto(user.getUsername(), user.getPassword());
  } 

  /**
   * Converts a UserDataDto to a User.
   * @param userDataDto the UserDataDto to convert
   * @return the corresponding User
   */
  public User fromDto(UserDataDto userDataDto) {
    if (userDataDto == null) {
      throw new IllegalArgumentException("UserDataDto cannot be null");
    }

    return new User(userDataDto.getUsername(), userDataDto.getPassword());
  }
}
