package com.gdsc.vridge.dto;

import lombok.Data;

@Data
public class CreateTTSDto {

    private String text;
    private String ttsId;
    private String uid;
    private String vid;
    private int pitch;

}
