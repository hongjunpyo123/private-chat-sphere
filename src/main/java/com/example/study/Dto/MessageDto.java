package com.example.study.Dto;

import com.example.study.Entity.MessageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private Long id;
    private Long chatRoomId;
    private String writer;
    private String message;
    private String Date;
    private String filePath;
    private String messageType;  // DB에 영향을 주지 않는 필드

    public MessageEntity toEntity(){
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(this.id);
        messageEntity.setChatRoomId(this.chatRoomId);
        messageEntity.setWriter(this.writer);
        messageEntity.setMessage(this.message);
        messageEntity.setDate(this.Date);
        messageEntity.setFilePath(this.filePath);
        return messageEntity;
    }
}
