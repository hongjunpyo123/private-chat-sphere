package com.example.study.Service;

import com.example.study.Dto.ChatRoomDto;
import com.example.study.Dto.UserDto;
import com.example.study.Entity.ChatRoomEntity;
import com.example.study.Entity.UserEntity;
import com.example.study.Repository.ChatRoomRepository;
import com.example.study.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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
        return chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Boolean ChatRoomInsert(ChatRoomDto chatRoomDto,HttpSession session){
        try{
            chatRoomRepository.save(chatRoomDto.toEntity());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public List<ChatRoomEntity> chatroom_find(String search){
        return chatRoomRepository.findByTitleContaining(search);
    }

    public ChatRoomEntity ChatFindById(Long id){
        return chatRoomRepository.findById(id).orElse(null);
    }

    public Boolean ChatPwCmp(Long id, String password){
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(id).orElse(null);
        if(chatRoomEntity.getPassword().equals(password)){
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean ChatPwEmpty(Long id){
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(id).orElse(null);
        if(chatRoomEntity.getPassword().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }




}
