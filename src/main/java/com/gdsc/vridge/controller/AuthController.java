package com.gdsc.vridge.controller;

import com.gdsc.vridge.dto.response.BooleanResponseDto;
import com.gdsc.vridge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BooleanResponseDto> login(@RequestBody String firebaseAuthToken) {
        boolean loginResult = authService.loginUser(firebaseAuthToken);
        BooleanResponseDto booleanResponseDto = BooleanResponseDto.builder()
            .success(loginResult)
            .build();
        if (loginResult) {
            return ResponseEntity.ok(booleanResponseDto);
        } else {
            return ResponseEntity.badRequest().body(booleanResponseDto);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<BooleanResponseDto> delete(@RequestBody String uid) {
        boolean deleteResult = authService.deleteUser(uid);
        BooleanResponseDto booleanResponseDto = BooleanResponseDto.builder()
            .success(deleteResult)
            .build();
        if (deleteResult) {
            return ResponseEntity.ok(booleanResponseDto);
        } else {
            return ResponseEntity.badRequest().body(booleanResponseDto);
        }
    }
}