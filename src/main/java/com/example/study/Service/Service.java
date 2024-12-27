package com.example.study.Service;

import com.example.study.Dto.UserDto;
import com.example.study.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class Service {
    @Autowired
    private UserRepository userRepository;

    public Boolean userDataInsert(UserDto userDto){
        try{
            userRepository.save(userDto.toEntity());
            userRepository.flush();
            return true;
        } catch (DataIntegrityViolationException e){
            System.out.println("Error: " + e);
            return false;
        }
    }
}
