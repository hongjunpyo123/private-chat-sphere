package com.example.study.Controller;

import com.example.study.Dto.MessageDto;
import com.example.study.Service.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @Autowired
    private Service service;

    @GetMapping("/messageFindLast")
    public MessageDto messageFindLast(HttpSession session){
        return service.messageFindLast(session);
    }
}
