package com.gdsc.vridge.controller;

import com.gdsc.vridge.dto.UserDto;
import com.gdsc.vridge.dto.UserDto.UserInfo;
import com.gdsc.vridge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginUser(@RequestBody UserDto.Login login){
        return userService.loginUser(login);
    }

    @PostMapping("/fcmToken")
    public ResponseEntity<Void> updateUserFcmToken(@RequestBody UserDto.FcmToken fcmToken){
        return userService.updateUserFcmToken(fcmToken);
    }

    @PostMapping("/unregister")
    public ResponseEntity<Void> deleteUser(@RequestBody String uid){
        return userService.deleteUser(uid);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfo> getUser(@RequestParam String uid){
        return userService.getUser(uid);
    }

}
