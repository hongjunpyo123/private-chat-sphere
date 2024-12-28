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

    public Boolean userDataInsert(UserDto userDto){ //회원가입
        try{
            if(userRepository.findBynickname(userDto.getNickname()).orElse(null) != null){
                return false; //중복된 닉네임이 존재할 경우 false 반환
            } else {
                userRepository.save(userDto.toEntity());
                return true; //닉네임이 중복되지 않았을 경우 데이터베이스에 저장 후 true 반환
            }
        } catch (DataIntegrityViolationException e){
            System.out.println("Error: " + e);
            return false;
        }
    }

    public Boolean userDataLogin(UserDto userDto, HttpSession session){ //로그인
        userEntity = userRepository.findBynickname(userDto.getNickname()).orElse(null);

        if(userEntity == null){
            return false;
        } else {
            if(userEntity.getPassword().equals(userDto.getPassword())){
                session.setAttribute("loginuser", userDto.getNickname()); //세션에 로그인한 유저의 닉네임 저장
                return true;
            } else {
                return false;
            }
        }
    }

    public List<ChatRoomEntity> chatroom_findAll(){
        return chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));//채팅방 목록을 최신순으로 정렬하여 반환
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

    public Boolean ChatPwEmpty(Long id){//채팅방 입장 비밀번호가 empty인지 확인 후 반환
        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(id).orElse(null);
        if(chatRoomEntity.getPassword().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
