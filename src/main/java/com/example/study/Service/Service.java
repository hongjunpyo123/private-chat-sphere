package com.example.study.Service;

import com.example.study.Dto.ChatRoomDto;
import com.example.study.Dto.MessageDto;
import com.example.study.Dto.UserDto;
import com.example.study.Entity.ChatRoomEntity;
import com.example.study.Entity.MessageEntity;
import com.example.study.Entity.UserEntity;
import com.example.study.Repository.ChatRoomRepository;
import com.example.study.Repository.MessageRepository;
import com.example.study.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.StyledEditorKit;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    private UserEntity userEntity = new UserEntity();

    public Boolean userDataInsert(UserDto userDto, HttpSession session){ //회원가입
        session.setAttribute("loginuser", userDto.getNickname());//회원가입
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
        chatRoomDto.setCount(0L); //채팅방 생성 시 count는 0으로 초기화
        try{
            chatRoomRepository.save(chatRoomDto.toEntity());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public List<ChatRoomEntity> chatroom_find(String search){
        if(search.isEmpty()){
            return chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
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

    @Transactional
    public Boolean userDataDelete(String nickname){
        try{
            userRepository.deleteBynickname(nickname);
            return true;
        } catch (Exception e){
            System.out.println("Error: " + e);
            return false;
        }
    }

    public List<MessageEntity> chatMessageFindAll(Long id){
        return messageRepository.findBychatRoomIdOrderByIdAsc(id); //id에 해당하는 채팅방 메시지를 id순으로 정렬하여 반환
    }

    public Boolean chatMessageInsert(MessageDto messageDto, HttpSession session) {
        messageDto.setDate(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))); //현재 시간 저장
        messageDto.setWriter((String) session.getAttribute("loginuser")); //세션에 저장된 로그인한 유저의 닉네임을 작성자로 저장
        try{
            messageRepository.save(messageDto.toEntity());
            return true;
        } catch (Exception e){
            System.out.println("Error: " + e);
            return false;
        }
    }

}
