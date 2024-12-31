package com.example.study.Repository;

import com.example.study.Entity.ChatRoomEntity;
import com.example.study.Entity.MessageEntity;
import org.aspectj.bridge.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findTop20ByChatRoomIdOrderByIdDesc(Long chatRoomId);
    MessageEntity findTopByChatRoomIdOrderByIdDesc(Long chatRoomId);
}
