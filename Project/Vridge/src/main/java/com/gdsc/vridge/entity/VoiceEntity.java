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
public class VoiceEntity {

    @Id
    private String vid;

    private String name;

    private int pitch;

    private String language;

    private boolean status;

}
