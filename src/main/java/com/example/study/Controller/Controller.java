package com.example.study.Controller;

import com.example.study.Dto.ChatRoomDto;
import com.example.study.Dto.UserDto;
import com.example.study.Entity.ChatRoomEntity;
import com.example.study.Service.Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    private Service service;

    @GetMapping("/")
    public String login(){
        return "login.html";
    }

    @PostMapping("/login_ok")
    public String login_ok(@ModelAttribute UserDto userDto, HttpSession session, Model model){
        if(service.userDataLogin(userDto, session)){
            main(model, session);
            return "main";
        } else {
            //System.out.println("Login failed");
            return "redirect:login.html";
        }
    }

    @GetMapping("/register")
    public String register(){
        return "register.html";
    }

    @GetMapping("/main")
    public String main(Model model, HttpSession session){
        List<ChatRoomEntity> chatRoomEntity = service.chatroom_findAll();
        for (ChatRoomEntity chatRoom : chatRoomEntity) {
            if(chatRoom.getPassword().isEmpty()){
                chatRoom.setPassword(null);
            }
        }

        model.addAttribute("loginuser", session.getAttribute("loginuser"));
        model.addAttribute("chatroom", chatRoomEntity);
        return "main";
    }

    @PostMapping("/register_ok")
    public String register_ok(@ModelAttribute UserDto userDto, HttpSession session){
        if(service.userDataInsert(userDto, session)){
          System.out.println("Register Success");
            return "redirect:/main";
        } else {
            System.out.println("Register failed");
            return "redirect:register.html";
        }
    }


    @PostMapping("/chatroom_create")
    public String chatroom_create(@ModelAttribute ChatRoomDto chatRoomDto, HttpSession session, Model model){
        chatRoomDto.setWriter((String) session.getAttribute("loginuser"));

        //-----title, password길이 검증 로직
        if(chatRoomDto.getTitle().length() <= 40 && chatRoomDto.getPassword().length() <= 10){
            service.ChatRoomInsert(chatRoomDto, session);
        }
        //-------------------------------

        return "redirect:/main";
    }

    @GetMapping("/chatroom_search")
    public String chatroom_search(@RequestParam String search, Model model, HttpSession session){
        model.addAttribute("loginuser", session.getAttribute("loginuser"));
        model.addAttribute("chatroom", service.chatroom_find(search));
        return "main";
    }

    @GetMapping("/mypage")
    public String mypage(Model model, HttpSession session){
        model.addAttribute("loginuser", session.getAttribute("loginuser"));
        return "mypage";
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable Long id, Model model, HttpSession session){
        session.setAttribute("chatid", id);
        model.addAttribute("loginuser", session.getAttribute("loginuser"));
        model.addAttribute("chat", service.ChatFindById(id));
        if(service.ChatPwEmpty(id)){
            return "chat";
        } else {
            return "chat_password";
        }
    }

    @GetMapping("/chat1")
    public String chat1(@RequestParam String password, Model model, HttpSession session){
        Long id = (Long) session.getAttribute("chatid");
        model.addAttribute("loginuser", session.getAttribute("loginuser"));
        model.addAttribute("chat", service.ChatFindById(id));
        model.addAttribute("chatroom", service.chatroom_findAll());
        if(service.ChatPwCmp(id, password)){
            return "chat";
        } else {
            return "main";
        }
    }

    @GetMapping("/DeleteAccount")
    public String DeleteAccount(HttpSession session){
        if(service.userDataDelete((String) session.getAttribute("loginuser"))){
            System.out.println("Delete Success");
        } else {
            System.out.println("Delete failed");
        }
        return "redirect:login.html";
    }
}
