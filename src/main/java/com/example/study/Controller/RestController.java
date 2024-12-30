package com.example.study.Controller;

import com.example.study.Dto.MessageDto;
import com.example.study.Service.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @Autowired
    private Service service;

    @GetMapping("/messageFindLast")
    public MessageDto messageFindLast(HttpSession session){
        return service.messageFindLast(session);
    }

    @GetMapping("/sessonNicknameChange/{loginuser}")
    public void sessonNicknameChange(@PathVariable String loginuser, HttpSession session){
        service.sessonNicknameChange(loginuser, session);
    }

}
