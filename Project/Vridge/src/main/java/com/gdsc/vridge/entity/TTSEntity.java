package com.gdsc.vridge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class TTSEntity {

    @Id
    private String tid;

    private String text;

    private Date timestamp;

    private boolean status;

}
