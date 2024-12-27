package com.example.study.Service;

import com.example.study.Dto.UserDto;
import com.example.study.Entity.ChatRoomEntity;
import com.example.study.Entity.UserEntity;
import com.example.study.Repository.ChatRoomRepository;
import com.example.study.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private UserEntity userEntity = new UserEntity();

    public Boolean userDataInsert(UserDto userDto){
        try{
            userRepository.save(userDto.toEntity());
            userRepository.flush();
            return true;
        } catch (DataIntegrityViolationException e){
            System.out.println("Error: " + e);
            return false;
        }
    }

    public Boolean userDataLogin(UserDto userDto, HttpSession session){
        userEntity = userRepository.findBynickname(userDto.getNickname()).orElse(null);

        if(userEntity == null){
            return false;
        } else {
            if(userEntity.getPassword().equals(userDto.getPassword())){
                session.setAttribute("loginuser", userDto.getNickname());
                return true;
            } else {
                return false;
            }
        }
    }

    public List<ChatRoomEntity> chatroom_findAll(){
        return chatRoomRepository.findAll();
    }




}
