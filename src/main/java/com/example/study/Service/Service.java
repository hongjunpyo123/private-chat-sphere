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
import com.example.study.Utility.Utility;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import java.io.File;
import java.io.IOException;
import java.lang.management.LockInfo;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Setter
@org.springframework.stereotype.Service
public class Service {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private Utility utility;

    private UserEntity userEntity = new UserEntity();
    private ChatRoomEntity chatRoomEntity = new ChatRoomEntity();

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

    public List<ChatRoomEntity> chatroom_findAll(HttpSession session){
        return chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));//채팅방 목록을 최신순으로 정렬하여 반환
    }

    public Boolean ChatRoomInsert(ChatRoomDto chatRoomDto,HttpSession session){
        MessageEntity messageEntity = new MessageEntity();
        ChatRoomEntity chatRoomEntity;

        try{
            chatRoomDto.setCount(0L);
            chatRoomEntity = chatRoomRepository.save(chatRoomDto.toEntity());
            chatRoomEntity.setPassword(utility.SimpleEncrypt(chatRoomEntity.getPassword()));
            messageEntity.setDate("00:00");
            messageEntity.setChatRoomId(chatRoomEntity.getId());
            messageEntity.setMessage(utility.encrypt("채팅방이 생성되었습니다.", utility.getEncryptKey()));
            messageEntity.setWriter("System");
            messageRepository.save(messageEntity);
            session.setAttribute("chatid", chatRoomEntity.getId());
            session.setAttribute("password", chatRoomEntity.getPassword());

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

    public ChatRoomEntity ChatFindById(Long id, HttpSession session){

        session.setAttribute("chatid", id);
        try{
            ChatRoomEntity chatRoomEntity1 = chatRoomRepository.findById((Long) session.getAttribute("chatid")).orElse(null);
            Long count = chatRoomEntity1.getCount();
            session.setAttribute("count", count);
        } catch (Exception e){
            return null; //엔티티가 null일 경우  / 방 입장 링크의 방이 존재하지 않을 경우
        }


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
        List<MessageEntity> messageEntityList = messageRepository.findTop20ByChatRoomIdOrderByIdDesc(id);//id에 해당하는 채팅방 메시지를 id순으로 20개 정렬하여 반환
        Collections.reverse(messageEntityList);//역순으로 정렬
        messageEntityList.forEach(messageEntity -> {
            //메세지 복호화
            messageEntity.setMessage(utility.decrypt(messageEntity.getMessage(), utility.getEncryptKey()));
            try{ //파일이 존재할 경우 복호화
                messageEntity.setFilePath(utility.decrypt(messageEntity.getFilePath(), utility.getEncryptKey()));
            } catch (Exception e){ }
        });
        return messageEntityList;
    }

    public Boolean chatMessageInsert(MessageDto messageDto, HttpSession session,  HttpServletResponse response) throws IOException {
        //채팅방을 찾고
        chatRoomEntity = chatRoomRepository.findById((Long) session.getAttribute("chatid")).orElse(null);

        //메세지가 없을 때 예외처리
        if (messageDto.getMessage().isEmpty()) {
            return false;
        }

        messageDto.setDate(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))); //현재 시간 저장
        messageDto.setWriter((String) session.getAttribute("loginuser")); //세션에 저장된 로그인한 유저의 닉네임을 작성자로 저장

        //메세지 암호화
        String encryptString = utility.encrypt(messageDto.getMessage(), utility.getEncryptKey());
        messageDto.setMessage(encryptString);

        try {
            messageRepository.save(messageDto.toEntity());

            //count를 1 증가시킨 후 저장
            try {
                Long count = chatRoomEntity.getCount() + 1;
                chatRoomEntity.setCount(count);
                chatRoomRepository.save(chatRoomEntity);
            } catch (Exception e) {
                response.sendRedirect("/err.html");
            }
            //----------------------------------------------


            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }


    //rest-api
    public MessageDto messageFindLast(HttpSession session) throws Exception{
        Long chatRoomId = (Long) session.getAttribute("chatid");
        String writer = (String) session.getAttribute("loginuser");
        Long count = (Long) session.getAttribute("count");


        Long chatRoomCount = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new Exception("ChatRoom이 존재하지 않습니다. ID: " + chatRoomId))
                .getCount(); //예외처리

        if(chatRoomCount == count){
            return null;
        }


        MessageDto messageDto = messageRepository.findTopByChatRoomIdOrderByIdDesc(chatRoomId).toDto();

        //메세지 복호화
        messageDto.setMessage(utility.decrypt(messageDto.getMessage(), utility.getEncryptKey()));
        try{ //파일이 존재할 경우 복호화
            messageDto.setFilePath(utility.decrypt(messageDto.getFilePath(), utility.getEncryptKey()));
        } catch (Exception e){ }

        if(messageDto.getWriter().equals(writer)){
            messageDto.setMessageType("sent");
        } else {
            messageDto.setMessageType("received");
        }

        session.setAttribute("count", count + 1);
        return messageDto;
    }

    public Boolean FileUpload(MultipartFile file, HttpSession session, MessageDto messageDto){
        String fileName = file.getOriginalFilename();
        Resource resource = resourceLoader.getResource("classpath:static/images");
        try {
            chatRoomEntity = chatRoomRepository.findById((Long) session.getAttribute("chatid")).orElse(null);


            String uploadDir = resource.getFile().getAbsolutePath();
            String filePath = "/images/" + fileName;
            file.transferTo(new File(uploadDir + File.separator + fileName));
            messageDto.setFilePath(utility.encrypt(filePath, utility.getEncryptKey())); //file url 암호화
            messageDto.setDate(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            messageDto.setWriter((String) session.getAttribute("loginuser"));
            messageDto.setMessage(utility.encrypt(session.getAttribute("loginuser") +
                    "님이 이미지를 전송하였습니다.", utility.getEncryptKey()));

            messageRepository.save(messageDto.toEntity());

            //채팅방을 찾고 count를 1 증가시킨 후 저장
            Long count = chatRoomEntity.getCount() + 1;
            chatRoomEntity.setCount(count);
            chatRoomRepository.save(chatRoomEntity);
            //----------------------------------------------

            return true;
        } catch (Exception e){
            System.out.println("Error: " + e);
            return false;
        }
    }

    public void sessonNicknameChange(String loginuser, HttpSession session){
        session.setAttribute("loginuser", loginuser);
    }
}
