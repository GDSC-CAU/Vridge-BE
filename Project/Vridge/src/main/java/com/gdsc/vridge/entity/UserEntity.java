package com.gdsc.vridge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserEntity {

    private String uid;

    private String name;

    private String email;

    private int cntVoice;

    private String recordVid;

    private int recordIndex;

    private String fcmToken;

}
