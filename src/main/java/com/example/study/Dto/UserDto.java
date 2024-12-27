package com.example.study.Dto;

import com.example.study.Entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String nickname;
    private String password;

    public UserEntity toEntity(){
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname(this.nickname);
        userEntity.setPassword(this.password);
        return userEntity;
    }
}
