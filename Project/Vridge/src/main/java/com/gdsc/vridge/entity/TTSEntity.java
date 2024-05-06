package com.gdsc.vridge.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TTSEntity {

    private String tid;

    private String text;

    private Date timestamp;

    private boolean status;

}
