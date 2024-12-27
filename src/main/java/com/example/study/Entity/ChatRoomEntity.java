package com.example.study.Entity;

import com.example.study.Dto.ChatRoomDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chatroom")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String writer;

    @Column(length = 255)
    private String password;

    public ChatRoomDto toDto(){
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setId(this.id);
        chatRoomDto.setTitle(this.title);
        chatRoomDto.setWriter(this.writer);
        chatRoomDto.setPassword(this.password);
        return chatRoomDto;
    }
}
