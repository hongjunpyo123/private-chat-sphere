package com.example.study.Repository;

import com.example.study.Entity.ChatRoomEntity;
import com.example.study.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    List<ChatRoomEntity> findByTitleContaining(String title);
}
