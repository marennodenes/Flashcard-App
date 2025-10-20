package dto.mappers;

import dto.UserDataDto;
import app.UserData;

public class UserMapper {
  
  private UserDataDto userDataDto;

  public UserMapper(UserDataDto userDataDto) {
    this.userDataDto = userDataDto;
  }

  public UserDataDto toDto(UserData userData) {
    if (userData.getDeckManager() == null) {
      return new UserDataDto(userData.getUsername(), userData.getPassword());
    }

    return new UserDataDto(userData.getUsername(), userData.getPassword(), userData.getDeckManager());
  } 


  public UserData fromDto(UserDataDto userDataDto) {
    return new UserData(userDataDto.getUsername(), userDataDto.getPassword(), userDataDto.getDeckManager());
  }
}
