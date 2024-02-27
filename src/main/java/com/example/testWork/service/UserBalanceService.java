package com.example.testWork.service;

import com.example.testWork.models.User;
import com.example.testWork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBalanceService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Scheduled(fixedRate = 5 * 60 * 1000) // Обновление раз в 5 минут
    public void updateAllUserBalances() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setBalance(user.getBalance() * 1.05);
        }
        userRepository.saveAll(users);
    }

}
