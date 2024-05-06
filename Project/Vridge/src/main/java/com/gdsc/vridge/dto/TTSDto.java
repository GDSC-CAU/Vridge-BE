package com.gdsc.vridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TTSDto {
    private String uid;
    private String vid;
    private String tid;
    private String text;
    private int pitch;
    private long timestamp;
}
