package com.example.study.Controller;

import com.example.study.Dto.MessageDto;
import com.example.study.Service.Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @Autowired
    private Service service;


    @GetMapping("/messageFindLast")
    public MessageDto messageFindLast(HttpSession session,HttpServletResponse response){
            try{
                MessageDto messageDto = service.messageFindLast(session);
                return messageDto;
            } catch (Exception e){
                try{
                    response.sendRedirect("/err.html");
                } catch (Exception f){}
            }
            return null;
    }

    @GetMapping("/sessonNicknameChange/{loginuser}")
    public void sessonNicknameChange(@PathVariable String loginuser, HttpSession session){
            service.sessonNicknameChange(loginuser, session);
    }

    @PostMapping("/chatMessageSend")
    public ResponseEntity<?> chatMessageSend(
            @ModelAttribute MessageDto messageDto,
            @RequestParam(value = "image", required = false) MultipartFile file,
            HttpSession session, HttpServletResponse response) {
        try {
            if (file != null && !file.isEmpty()) { // 파일이 존재할 경우
                service.FileUpload(file, session, messageDto);
            } else {
                service.chatMessageInsert(messageDto, session, response);
            }

            // 성공 응답 반환
            return ResponseEntity.ok().body(Map.of(
                    "status", "success",
                    "chatRoomId", messageDto.getChatRoomId(),
                    "message", "Message sent successfully"
            ));
        } catch (Exception e) {
            e.printStackTrace();

            // 에러 응답 반환
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Failed to send message",
                    "error", e.getMessage()
            ));
        }
    }


}
