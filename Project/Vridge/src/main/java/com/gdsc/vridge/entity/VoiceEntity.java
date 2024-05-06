package com.gdsc.vridge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VoiceEntity {

    private String vid;

    private String name;

    private int pitch;

    private String language;

    private boolean status;

}
