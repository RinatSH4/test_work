package com.example.testWork.controllers;


import com.example.testWork.models.AuthRequest;
import com.example.testWork.models.User;
import com.example.testWork.repository.UserRepository;
import com.example.testWork.service.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/auth")
public class UserController {

    //private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UsersService servise;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/welcome")
    public String welcome() {
        //logger.info("welcome");
        return "welcome";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody User user) {

        if (userRepository.findUserByName(user.getName()) != null)
            return "Пользователь с таким именем уже существует";
        else if (userRepository.findByPhone(user.getPhone()) != null)
            return "Пользователь с таким телефоном уже существует";
        else if (userRepository.findByEmail(user.getEmail()) != null)
            return "Пользователь с таким email уже существует";
        else {
            //logger.info("Новый пользователь: " + user.getName());
            return servise.addUser(user);
        }
    }

    @PutMapping("/updateUser")
    public String updateUser(@RequestBody User user,
                             @AuthenticationPrincipal UsersDetails usersDetails) {
        User updateUser = userRepository.findByName(usersDetails.getUsername()).orElseGet(User::new);
        if ((userRepository.findByEmail(user.getEmail()) != null
                && !updateUser.getEmail().equals(user.getEmail())) ||
                (userRepository.findByPhone(user.getPhone()) != null
                        && !updateUser.getPhone().equals(user.getPhone())))
            return "Пользователь с таким email или телефоном уже существует";
        else {
            updateUser.setFirstName(user.getFirstName());
            updateUser.setLastName(user.getLastName());
            updateUser.setFathersName(user.getFathersName());
            updateUser.setPhone(user.getPhone());
            updateUser.setEmail(user.getEmail());
            updateUser.setBirthDate(user.getBirthDate());
            userRepository.save(user);
            //logger.info("Обновление данный пользователя: " + user.getName());
            return "Данные обновлены";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> user(@AuthenticationPrincipal UsersDetails usersDetails) {
        User user = userRepository.findByName(usersDetails.getUsername()).orElseGet(User::new);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, String>>> users() {
        List<Map<String, String>> allUsers = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name:", user.getName());
            userMap.put("email:", user.getEmail());
            userMap.put("phone:", user.getPhone());
            userMap.put("balance:", String.valueOf(user.getBalance()));
            allUsers.add(userMap);
        }
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/balance")
    public String balance(@AuthenticationPrincipal UsersDetails usersDetails) {
        User user = userRepository.findByName(usersDetails.getUsername()).orElseGet(User::new);
        return "Баланс: " + user.getBalance();
    }

    @PutMapping("/transfermoney")
    public String transferMoney(@AuthenticationPrincipal UsersDetails usersDetails,
                                @RequestBody TransferMoneyService transerMoney) {
        String responce;
        //quantity - сумма перевода
        User sender = userRepository.findByName(usersDetails.getUsername()).orElseGet(User::new);
        User recipient = userRepository.findByName(transerMoney.getUsername()).orElseGet(User::new);
        if (sender.getBalance() < transerMoney.getQuantity()) {
            responce = "На вашем счете недостаточно средств";
        }
        else {
            recipient.setBalance(recipient.getBalance() + transerMoney.getQuantity());
            sender.setBalance(sender.getBalance() - transerMoney.getQuantity());
            userRepository.save(recipient);
            userRepository.save(sender);
            responce = "Перевод на сумму " + transerMoney.getQuantity()
                    + " р. пользователю " + transerMoney.getUsername() + " выполнен. \n" +
                    "Ваш баланс " + sender.getBalance() + " р.";
        }
        return responce;
    }

    @PostMapping("/generateToken")
    public String generateToken(@RequestBody AuthRequest authReques){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authReques.getUsername(),
                        authReques.getPassword()));
        if (authentication.isAuthenticated()) {

            return jwtService.generateToken(authReques.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/findPhone")
    public ResponseEntity<User> findPhone(@RequestParam String phone) {
        User user = userRepository.findByPhone(phone);
        if (user.getPhone() != null)
            return ResponseEntity.ok(user);
        else return null;
    }

    @PostMapping("/findEmail")
    public ResponseEntity<User> findEmail(@RequestBody SearchService email) {
        User user = userRepository.findByEmail(email.getEmail());
        if (user.getEmail() != null)
            return ResponseEntity.ok(user);
        else return null;
    }

    @PostMapping("/findFullName")
    public ResponseEntity<List<User>> findFullName(@RequestBody SearchService fullName) {
        List<User> users = userRepository.searchUsers(fullName.getFullName());
        return ResponseEntity.ok(users);
    }
}
