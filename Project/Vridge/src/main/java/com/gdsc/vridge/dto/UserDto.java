package com.gdsc.vridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    public static class Login {
        private String token;
        private String fcmToken;
    }

    @Getter
    @Setter
    public static class FcmToken {
        private String uid;
        private String fcmToken;
    }

    @AllArgsConstructor
    public static class UserInfo {
        private String uid;
        private String name;
        private String email;
        private int cntVoice;
    }

}
