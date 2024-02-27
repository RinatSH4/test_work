package com.example.testWork.service;


import com.example.testWork.models.User;
import com.example.testWork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UsersDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userDetail = repository.findByName(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UsersDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не существует!"));
    }

    public String addUser(User userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInfo.setBalance(100.d);
        repository.save(userInfo);
        return "Пользователь " + userInfo.getName() + " зарегистрирован";
    }
}
