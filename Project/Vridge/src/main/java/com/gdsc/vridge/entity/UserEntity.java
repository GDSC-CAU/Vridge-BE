package com.gdsc.vridge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class UserEntity {

    @Id
    private String uid;

    private String name;

    private String email;

    private int cntVoice;

    private String recordVid;

    private int recordIndex;

    private String fcmToken;

}
