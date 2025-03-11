package com.example.study.Entity;

import com.example.study.Dto.MessageDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@Entity
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id")  // DB 컬럼명은 snake_case 유지
    private Long chatRoomId;

    @Column(length = 255)
    private String writer;

    @Column(length = 1023)
    private String message;

    @Column(length = 255)
    private String Date;

    @Column(length = 255)
    private String filePath;

    public MessageDto toDto(){
        MessageDto messageDto = new MessageDto();
        messageDto.setId(this.id);
        messageDto.setChatRoomId(this.chatRoomId);
        messageDto.setWriter(this.writer);
        messageDto.setMessage(this.message);
        messageDto.setDate(this.Date);
        messageDto.setFilePath(this.filePath);
        return messageDto;
    }




}
