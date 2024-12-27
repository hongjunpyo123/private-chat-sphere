package com.example.study.Entity;

import com.example.study.Dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
public class UserEntity {
    @Id
    @Column(length = 255, unique = true)
    private String nickname;

    @Column(length = 255)
    private String password;

    public UserDto toDto(){
        UserDto userDto = new UserDto();
        userDto.setNickname(this.nickname);
        userDto.setPassword(this.password);
        return userDto;
    }
}
