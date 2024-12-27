package com.example.study.Dto;


import com.example.study.Entity.ChatRoomEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDto {
    private long id;
    private String title;
    private String writer;
    private String password;

    public ChatRoomEntity toEntity(){
        ChatRoomEntity chatRoomEntity = new ChatRoomEntity();
        chatRoomEntity.setId(this.id);
        chatRoomEntity.setTitle(this.title);
        chatRoomEntity.setWriter(this.writer);
        chatRoomEntity.setPassword(this.password);
        return chatRoomEntity;
    }
}
