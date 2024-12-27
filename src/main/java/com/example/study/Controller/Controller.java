package com.example.study.Controller;

import com.example.study.Dto.UserDto;
import com.example.study.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/")
    public String login(){
        return "login.html";
    }

    @GetMapping("/register")
    public String register(){
        return "register.html";
    }

    @PostMapping("/register_ok")
    public String register_ok(@ModelAttribute UserDto userDto){
        if(service.userDataInsert(userDto)){
          System.out.println("Register Success");
          return "redirect:login.html";
        } else {
            System.out.println("Register failed");
            return "redirect:register.html";
        }
    }
}
