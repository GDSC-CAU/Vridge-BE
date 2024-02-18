package com.gdsc.vridge.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoiceListResponseDto {

    private List<String> voiceList;

}
