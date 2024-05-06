package com.gdsc.vridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class VoiceDto {

    @Getter
    @Setter
    public static class Upload {
        private String uid;
        private String vid;
        private int index;
    }

    @Getter
    @Setter
    public static class Finish {
        private String uid;
        private String vid;
        private String name;
        private int pitch;
        private String language;
    }

    @Getter
    @Setter
    public static class Synthesize {
        private String uid;
        private String[] vids;
        private String name;
        private int pitch;
        private String language;
    }

    @AllArgsConstructor
    public static class RecordInfo {
        private String uid;
        private String vid;
        private int recordIndex;
    }

}
